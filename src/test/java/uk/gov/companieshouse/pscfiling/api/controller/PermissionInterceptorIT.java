package uk.gov.companieshouse.pscfiling.api.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("web")
@WebMvcTest(controllers = PscIndividualFilingControllerImpl.class)
class PermissionInterceptorIT extends BaseControllerIT {
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
    private PscMapper filingMapper;
    @MockBean
    private Clock clock;
    @MockBean
    private Logger logger;


    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
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
