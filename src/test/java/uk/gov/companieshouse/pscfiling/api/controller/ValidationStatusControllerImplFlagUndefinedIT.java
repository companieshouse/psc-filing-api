package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.companieshouse.pscfiling.api.controller.ValidationStatusControllerImpl.TRANSACTION_NOT_SUPPORTED_ERROR;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

//Using Spring Web MVC
@Tag("web")
@WebMvcTest(controllers = ValidationStatusControllerImpl.class)
class ValidationStatusControllerImplFlagUndefinedIT {
    private static final String TRANS_ID = "4f56fdf78b357bfc";
    private static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    private static final String PASS_THROUGH_HEADER = "passthrough";

    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private FilingValidationService filingValidationService;
    @MockBean
    private PscIndividualMapper filingMapper;
    @MockBean
    private ErrorMapper errorMapper;
    @MockBean
    private Logger logger;
    private HttpHeaders httpHeaders;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASS_THROUGH_HEADER);
    }

    @Test
    void validateWhenFeatureFlagIsUndefined() throws Exception {
        final var transaction = new Transaction();
        final var filing = PscIndividualFiling.builder()
                .referenceEtag("etag")
                .referencePscId("id")
                .ceasedOn(LocalDate.of(2022, 9, 13))
                .build();

        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation_status", TRANS_ID, FILING_ID)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is(TRANSACTION_NOT_SUPPORTED_ERROR)));
    }

    @Test
    @DisplayName("Test related to bug PSC-118")
    void expectNotFoundResponseWhenPathInvalid() throws Exception {
        mockMvc.perform(get("/transactions/{transId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation", TRANS_ID, FILING_ID)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }


}