package uk.gov.companieshouse.pscfiling.api.controller.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.ValidatorConfig;
import uk.gov.companieshouse.pscfiling.api.config.enumerations.PscFilingConfig;
import uk.gov.companieshouse.pscfiling.api.error.RestExceptionHandler;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.service.impl.FilingValidationServiceImpl;

@Tag("app")
@SpringBootTest(classes = {
    ValidationStatusControllerImpl.class,
    FilingValidationServiceImpl.class,
    RestExceptionHandler.class
}, properties = {"feature.flag.transactions.closable=true"})
@Import(PscFilingConfig.class)
@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ValidatorConfig.class})
@ComponentScan(basePackages = {
        "uk.gov.companieshouse.pscfiling.api.validator",
        "uk.gov.companieshouse.pscfiling.api.mapper"
})
class ValidationStatusControllerImplValidationIT extends BaseControllerIT {
    private static final String SELF_FRAGMENT =
            "/transactions/" + TRANS_ID + "/persons-with-significant-control/";

    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private Logger logger;
    @Autowired
    private MockMvc mockMvc;
    private PscIndividualFiling filing;
    @Mock
    private PscApi pscDetails;

    @BeforeEach
    void setUp() throws Exception {
        baseSetUp();
        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment(PscTypeConstants.INDIVIDUAL.getValue())
                .pathSegment(FILING_ID)
                .build()
                .toUri();
        final Links links = new Links(self, null);
        filing = PscIndividualFiling.builder()
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .links(links)
                .build();
    }

    @Test
    void validateWhenDataValid() throws Exception {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.minusDays(1));
        when(pscDetails.getEtag()).thenReturn(ETAG);

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(true)))
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    @Test
    void validateWhenPscDetailsNotFound() throws Exception {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("stub PSC not found", null));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Reference ID for PSC not found")))
                .andExpect(jsonPath("$.errors[0].location", is("$.reference_psc_id")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenPscEtagNotMatched() throws Exception {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn("different-etag");

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("ETag for PSC must match the latest value")))
                .andExpect(jsonPath("$.errors[0].location", is("$.reference_etag")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenCeasedOnBeforePscNotifiedOn() throws Exception {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.plusDays(1));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Ceased date must be on or after the date the PSC was added")))
                .andExpect(jsonPath("$.errors[0].location", is("$.ceased_on")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenRegisterEntryDateBeforeCeasedOn() throws Exception {
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.minusDays(1));

        final PscIndividualFiling invalid =
                PscIndividualFiling.builder(filing).registerEntryDate(CEASED_ON_DATE.minusDays(1))
                        .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(invalid));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("PSC register entry date must be on or after the date the PSC was ceased")))
                .andExpect(jsonPath("$.errors[0].location", is("$.register_entry_date")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenPscNotActive() throws Exception {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.minusDays(1));
        when(pscDetails.getCeasedOn()).thenReturn(CEASED_ON_DATE);

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.is_valid", is(false)))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].error", is("PSC is already ceased")))
            .andExpect(jsonPath("$.errors[0].location", is("$.ceased_on")))
            .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
            .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    @DisplayName("handles psc details service unavailable error gracefully")
    void handlePscDetailsServiceUnavailable() throws Exception {
        // simulate exception caused by chs psc api service unavailable
        // caused by JSON parse error in api-sdk-java
        final var cause = new IllegalArgumentException(
            "expected numeric type but got class uk.gov.companieshouse.api.error.ApiErrorResponse");
        final var sdkException = new IllegalArgumentException("",
            cause); // message intentionally blank

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
            transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
            PASSTHROUGH_HEADER)).thenThrow(
            sdkException); // not caught but propagated to RestExceptionHandler

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(header().doesNotExist("location"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].error", is("Service Unavailable: {error}")))
            // note: property names not converted to snake case in this test because of
            // @EnableWebMvc
            .andExpect(
                jsonPath("$.errors[0].errorValues",
                    hasEntry("error", "Internal server error")))
            .andExpect(jsonPath("$.errors[0].type", is("ch:service")))
            .andExpect(jsonPath("$.errors[0].locationType", is("resource")));
    }

}