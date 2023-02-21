package uk.gov.companieshouse.pscfiling.api.controller;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValidChain;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.answerVoid;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.pscfiling.api.controller.PscIndividualFilingControllerImpl.VALIDATION_STATUS;
import static uk.gov.companieshouse.pscfiling.api.model.entity.Links.PREFIX_PRIVATE;

@ExtendWith(MockitoExtension.class)
class PscWithIdentificationFilingControllerImplTest {
    public static final String TRANS_ID = "117524-754816-491724";
    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private static final PscTypeConstants PSC_TYPE = PscTypeConstants.CORPORATE_ENTITY;
    private static final String PASSTHROUGH_HEADER = "passthrough";
    public static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    private static final URI REQUEST_URI =
            URI.create("/transactions/" + TRANS_ID + "/persons-with-significant-control/");
    private static final Instant FIRST_INSTANT = Instant.parse("2022-10-15T09:44:08.108Z");

    private PscWithIdentificationFilingController testController;
    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private Clock clock;
    @Mock
    private Logger logger;
    @Mock
    private PscMapper filingMapper;
    @Mock
    private PscWithIdentificationDto dto;
    @Mock
    private BindingResult result;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Transaction transaction;
    @Mock
    private PscExistsValidator pscExistsValidator;
    @Mock
    private FilingForPscTypeValidChain filingForPscTypeValidChain;
    @Mock
    private FilingValidationService filingValidationService;

    private PscWithIdentificationFiling filing;
    private Links links;
    private Map<String, Resource> resourceMap;
    private List<FieldError> validationErrors;
    private String[] bindingErrorCodes;
    private FieldError fieldErrorWithRejectedValue;

    @BeforeEach
    void setUp() {
        testController = new PscWithIdentificationFilingControllerImpl(transactionService, pscFilingService,
                filingMapper, clock, logger) {
        };
        filing = PscWithIdentificationFiling.builder()
                .referencePscId(PSC_ID)
                .referenceEtag("etag")
                .ceasedOn(LocalDate.parse("2022-09-13"))
                .build();
        final var builder = UriComponentsBuilder.fromUri(REQUEST_URI);
        links = new Links(builder.pathSegment(FILING_ID)
                .build().toUri(), builder.pathSegment("validation_status")
                .build().toUri());
        resourceMap = createResources();
        validationErrors = new ArrayList<>();
        bindingErrorCodes = new String[]{"code1", "code2.name", "code3"};
        fieldErrorWithRejectedValue =
                new FieldError("object", "field", "rejectedValue", false, bindingErrorCodes, null,
                        "errorWithRejectedValue");
    }

    @ParameterizedTest(name = "[{index}] null binding result={0}")
    @ValueSource(booleans = {true, false})
    void createFiling(final boolean nullBindingResult) {
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);
        when(filingMapper.map(dto)).thenReturn(filing);
        when(transaction.getId()).thenReturn(TRANS_ID);

        final var withFilingId = PscWithIdentificationFiling.builder(filing).id(FILING_ID)
                .build();
        final var withLinks = PscWithIdentificationFiling.builder(withFilingId).links(links)
                .build();
        when(pscFilingService.save(filing, TRANS_ID)).thenReturn(withFilingId);
        when(pscFilingService.save(withLinks, TRANS_ID)).thenReturn(withLinks);
        when(request.getRequestURI()).thenReturn(REQUEST_URI.toString());
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        final var response = testController.createFiling(PscTypeConstants.CORPORATE_ENTITY, dto,
                transaction, nullBindingResult ? null : result, request);

        // refEq needed to compare Map value objects; Resource does not override equals()
        verify(transaction).setResources(refEq(resourceMap));
        verify(transactionService).updateTransaction(transaction, PASSTHROUGH_HEADER);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    void createFilingWhenRequestHasBindingError() {
        when(result.getFieldErrors()).thenReturn(List.of(fieldErrorWithRejectedValue));
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);

        final var exception = assertThrows(InvalidFilingException.class,
                () -> testController.createFiling(PscTypeConstants.CORPORATE_ENTITY, dto,
                        transaction, result, request));

        assertThat(exception.getFieldErrors(), contains(fieldErrorWithRejectedValue));
    }

    private Map<String, Resource> createResources() {
        final Map<String, Resource> resourceMap = new HashMap<>();
        final var resource = new Resource();
        final var self = REQUEST_URI + "/" + FILING_ID;
        final var linksMap = Map.of("resource", self, VALIDATION_STATUS,
                PREFIX_PRIVATE + REQUEST_URI + "/" + FILING_ID + "/" + VALIDATION_STATUS);

        resource.setKind("psc-filing");
        resource.setLinks(linksMap);
        resource.setUpdatedAt(FIRST_INSTANT.atZone(ZoneId.systemDefault()).toLocalDateTime());
        resourceMap.put(self, resource);

        return resourceMap;
    }

    @Test
    void getFilingForReviewWhenFound() {

        when(filingMapper.map((PscCommunal) filing)).thenReturn(dto);

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var response = testController.getFilingForReview(TRANS_ID, PSC_TYPE, FILING_ID);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(dto));
    }

    @Test
    void getFilingForReviewNotFound() {

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        final var response = testController.getFilingForReview(TRANS_ID, PSC_TYPE, FILING_ID);

        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }
}
