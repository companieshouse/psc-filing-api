package uk.gov.companieshouse.pscfiling.api.controller.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.enumerations.PscFilingConfig;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("web")
@WebMvcTest(controllers = PscIndividualFilingControllerImpl.class)
@Import(PscFilingConfig.class)
class RestExceptionHandlerIT extends BaseControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private FilingValidationService filingValidationService;
    @MockBean
    private PscApi pscDetails;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private PscIndividualFilingService pscIndividualFilingService;
    @MockBean
    private PscMapper filingMapper;
    @MockBean
    private Clock clock;
    @MockBean
    private Logger logger;


    @BeforeEach
    void setUp() throws Exception {
        baseSetUp();
    }

    @Override
    protected void setupEricTokenPermissions() {
        // don't add any ERIC permissions
    }

    @Test
    @DisplayName("handles transaction service unavailable error gracefully")
    void handleTransactionServiceUnavailable() throws Exception {
        final var body = "{" + PSC07_FRAGMENT + "}";

        // simulate exception caused by transaction-api service unavailable
        // caused by JSON parse error in api-sdk-java
        final var cause = new IllegalArgumentException(
            "expected numeric type but got class uk.gov.companieshouse.api.error.ApiErrorResponse");

        when(transactionInterceptor.preHandle(any(), any(), any())).thenThrow(
            new IllegalArgumentException("", cause)); // message intentionally blank

        mockMvc.perform(post(URL_PSC_INDIVIDUAL, TRANS_ID).content(body)
                .contentType(APPLICATION_JSON)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isInternalServerError())
            .andExpect(header().doesNotExist("location"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].error", is("Service Unavailable: {error}")))
            .andExpect(
                jsonPath("$.errors[0].error_values", hasEntry("error", "Internal server error")))
            .andExpect(jsonPath("$.errors[0].type", is("ch:service")))
            .andExpect(jsonPath("$.errors[0].location_type", is("resource")));
    }

}
