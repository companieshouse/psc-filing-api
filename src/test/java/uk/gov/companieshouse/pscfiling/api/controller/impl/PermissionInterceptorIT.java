package uk.gov.companieshouse.pscfiling.api.controller.impl;

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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
class PermissionInterceptorIT extends BaseControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;
    @MockitoBean
    private PscDetailsService pscDetailsService;
    @MockitoBean
    private FilingValidationService filingValidationService;
    @MockitoBean
    private PscApi pscDetails;
    @MockitoBean
    private PscFilingService pscFilingService;
    @MockitoBean
    private PscIndividualFilingService pscIndividualFilingService;
    @MockitoBean
    private PscMapper filingMapper;
    @MockitoBean
    private Clock clock;
    @MockitoBean
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
    @DisplayName("permission interceptor rejects request without required permission")
    void permissionInterceptorRejectsRequestWithoutPermission() throws Exception {
        final var body = "{" + PSC07_FRAGMENT + "}";

        mockMvc.perform(post(URL_PSC_INDIVIDUAL, TRANS_ID).content(body)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("location"))
                .andExpect(jsonPath("$").doesNotExist());
    }

}
