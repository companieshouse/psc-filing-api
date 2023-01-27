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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.IntegrationTestConfig;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationServiceImpl;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("app")
@WebMvcTest(controllers = ValidationStatusControllerImpl.class,
        properties = {"feature.flag.transactions.closable=true"})
@ContextConfiguration(classes = {IntegrationTestConfig.class, FilingValidationServiceImpl.class})
class ValidationStatusControllerImplFlagTrueIT extends BaseControllerIT {
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private Logger logger;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void validateWhenFilingNotFound() throws Exception {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/transactions/{transactionId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

}