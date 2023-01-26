package uk.gov.companieshouse.pscfiling.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.ValidatorConfig;
import uk.gov.companieshouse.pscfiling.api.error.RestExceptionHandler;
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
class ValidationStatusControllerImplFlagTrueIT {
    private static final String TRANS_ID = "4f56fdf78b357bfc";
    private static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    private static final String PASS_THROUGH_HEADER = "passthrough";

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

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASS_THROUGH_HEADER);
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");
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

}