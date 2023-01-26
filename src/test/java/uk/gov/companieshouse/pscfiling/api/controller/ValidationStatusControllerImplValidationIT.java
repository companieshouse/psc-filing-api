package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.ValidatorConfig;
import uk.gov.companieshouse.pscfiling.api.error.RestExceptionHandler;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationServiceImpl;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("app")
@SpringBootTest(classes = {
        ValidationStatusControllerImpl.class,
        FilingValidationServiceImpl.class,
        RestExceptionHandler.class
}, properties = {"feature.flag.transactions.closable=true"})
@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ValidatorConfig.class})
@ComponentScan(basePackages = {
        "uk.gov.companieshouse.pscfiling.api.validator",
        "uk.gov.companieshouse.pscfiling.api.mapper"
})
class ValidationStatusControllerImplValidationIT {
    private static final String TRANS_ID = "4f56fdf78b357bfc";
    private static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    private static final String PASS_THROUGH_HEADER = "passthrough";
    private static final String SELF_FRAGMENT =
            "/transactions/" + TRANS_ID + "/persons-with-significant-control/";
    private static final LocalDate DATE = LocalDate.of(2020, 5, 10);
    private static final String ETAG = "e7101610f832de81c8d2f27904d6b1de2be82ff6";

    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private Logger logger;
    private HttpHeaders httpHeaders;
    @Autowired
    private MockMvc mockMvc;
    private Transaction transaction;
    private PscIndividualFiling filing;
    @Mock
    private PscApi pscDetails;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASS_THROUGH_HEADER);
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");

        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment(PscTypeConstants.INDIVIDUAL.getValue())
                .pathSegment(FILING_ID)
                .build()
                .toUri();
        final Links links = new Links(self, null);
        filing = PscIndividualFiling.builder()
                .referenceEtag(ETAG)
                .referencePscId("id")
                .ceasedOn(DATE)
                .links(links)
                .build();
    }

    @Test
    void validateWhenDataValid() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASS_THROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PscTypeConstants.INDIVIDUAL,
                PASS_THROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getNotifiedOn()).thenReturn(DATE);
        when(pscDetails.getEtag()).thenReturn(ETAG);

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(true)))
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    @Test
    void validateWhenPscDetailsNotFound() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASS_THROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PscTypeConstants.INDIVIDUAL,
                PASS_THROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("stub PSC not found", null));

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("PSC with that reference ID was not found")))
                .andExpect(jsonPath("$.errors[0].location", is("$.reference_psc_id")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenPscEtagNotMatched() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASS_THROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PscTypeConstants.INDIVIDUAL,
                PASS_THROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn("different-etag");

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Etag for PSC does not match latest value")))
                .andExpect(jsonPath("$.errors[0].location", is("$.reference_etag")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenCeasedOnBeforePscNotifiedOn() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASS_THROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PscTypeConstants.INDIVIDUAL,
                PASS_THROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(DATE.plusDays(1));

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Ceased on date cannot be before the date the PSC was notified on")))
                .andExpect(jsonPath("$.errors[0].location", is("$.ceased_on")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenRegisterEntryDateBeforeCeasedOn() throws Exception {
        when(transactionService.getTransaction(TRANS_ID, PASS_THROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PscTypeConstants.INDIVIDUAL,
                PASS_THROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(DATE);
        PscIndividualFiling invalid =
                PscIndividualFiling.builder(filing).registerEntryDate(DATE.minusDays(1)).build();
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(invalid));

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("PSC register entry date cannot be before the cessation date")))
                .andExpect(jsonPath("$.errors[0].location", is("$.register_entry_date")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

}