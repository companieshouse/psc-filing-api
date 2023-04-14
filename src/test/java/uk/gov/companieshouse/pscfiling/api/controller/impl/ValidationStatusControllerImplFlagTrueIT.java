package uk.gov.companieshouse.pscfiling.api.controller.impl;

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
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.service.impl.FilingValidationServiceImpl;

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
        baseSetUp();
    }

    @Test
    void validateWhenFilingNotFound() throws Exception {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

}