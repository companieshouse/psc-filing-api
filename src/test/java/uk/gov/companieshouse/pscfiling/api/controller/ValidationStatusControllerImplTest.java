package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.pscfiling.api.controller.ValidationStatusControllerImpl.TRANSACTION_NOT_SUPPORTED_ERROR;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@ExtendWith(MockitoExtension.class)
class ValidationStatusControllerImplTest {

    private static final String TRANS_ID = "117524-754816-491724";
    private static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    private static final String PASSTHROUGH_HEADER = "passthrough";
    private static final String SELF_FRAGMENT =
            "/transactions/" + TRANS_ID + "/persons-with-significant-control/";

    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private FilingValidationService filingValidationService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Logger logger;
    @Mock
    private PscMapper filingMapper;
    @Mock
    private ErrorMapper errorMapper;
    @Mock
    private Transaction transaction;

    private ValidationStatusControllerImpl testController;


    @BeforeEach
    void setUp() {
        testController = new ValidationStatusControllerImpl(pscFilingService,
                filingValidationService, transactionService, filingMapper, errorMapper, true, logger);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);
    }

    @Test
    void validateWhenClosableFlagFalse() {
        testController = new ValidationStatusControllerImpl(pscFilingService,
                filingValidationService, transactionService, filingMapper, errorMapper, false, logger);
        final var filing = PscIndividualFiling.builder()
                .build();
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var response = testController.validate(TRANS_ID, FILING_ID, transaction, request);

        assertThat(response.isValid(), is(false));
        assertThat(response.getValidationStatusError(), is(arrayWithSize(1)));
        assertThat(response.getValidationStatusError()[0].getError(),
                is(TRANSACTION_NOT_SUPPORTED_ERROR));
    }

    @Test
    void validateWhenFilingNotFound() {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        final var filingResourceNotFoundException =
                assertThrows(FilingResourceNotFoundException.class,
                        () -> testController.validate(TRANS_ID, FILING_ID, transaction, request));

        assertThat(filingResourceNotFoundException.getMessage(), containsString(FILING_ID));
    }

    @Test
    void validateWhenPscTypeNotIdentified() {
        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment("bad-psc-type")
                .pathSegment(FILING_ID)
                .build()
                .toUri();
        final Links links = new Links(self, null);
        final var filing = PscIndividualFiling.builder().links(links)
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var response = testController.validate(TRANS_ID, FILING_ID, transaction, request);
        final var expectedError =
                new ValidationStatusError("PSC type could not be identified", "$.links.self", "resource",
                        "ch:validation");

        assertThat(response.isValid(), is(false));
        assertThat(response.getValidationStatusError(), is(arrayContaining(samePropertyValuesAs(expectedError))));
    }

    @Test
    void validateWhenFilingValid()
    {
        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment(PscTypeConstants.INDIVIDUAL.getValue())
                .pathSegment(FILING_ID)
                .build()
                .toUri();
        final Links links = new Links(self, null);
        final PscCommunal filing = PscIndividualFiling.builder().links(links)
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var dto = PscIndividualDto.builder().build();
        when(filingMapper.map(filing)).thenReturn(dto);
        when(errorMapper.map(anyList())).thenReturn(new ValidationStatusError[0]);

        final var response = testController.validate(TRANS_ID, FILING_ID, transaction, request);

        assertThat(response.isValid(), is(true));
        assertThat(response.getValidationStatusError(), is(emptyArray()));

    }

}