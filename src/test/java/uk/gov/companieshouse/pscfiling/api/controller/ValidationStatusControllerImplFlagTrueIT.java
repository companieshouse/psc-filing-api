package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.IntegrationTestConfig;
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
@ContextConfiguration(classes = {ValidatorConfig.class, IntegrationTestConfig.class})
class ValidationStatusControllerImplFlagTrueIT {
    private static final String TRANS_ID = "4f56fdf78b357bfc";
    private static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    private static final String PASS_THROUGH_HEADER = "passthrough";
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
    private HttpHeaders httpHeaders;
    @Autowired
    private MockMvc mockMvc;
    private Transaction transaction;
    private PscIndividualFiling filing;

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
                .referenceEtag("etag")
                .referencePscId("id")
                .ceasedOn(LocalDate.of(2022, 9, 13))
                .links(links)
                .build();
    }

    @Test
    void validateWhenFeatureFlagIsTrue() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASS_THROUGH_HEADER)).thenReturn(
                transaction);
        final var dummyPsc = new PscApi();
        when(pscDetailsService.getPscDetails(transaction, "id", PscTypeConstants.INDIVIDUAL,
                PASS_THROUGH_HEADER)).thenReturn(dummyPsc);

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{\"is_valid\":%s, \"errors\": []}", true)))
                .andExpect(jsonPath("$.is_valid", is(true)))
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    @Test
    void validateWhenFilingNotFound() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void validateWhenNotValid() throws Exception {
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

}