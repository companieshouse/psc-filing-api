package uk.gov.companieshouse.pscfiling.api.controller.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.companieshouse.pscfiling.api.controller.impl.ValidationStatusControllerImpl.TRANSACTION_NOT_SUPPORTED_ERROR;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.enumerations.PscFilingConfig;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

//Using Spring Web MVC
@Tag("web")
@WebMvcTest(controllers = ValidationStatusControllerImpl.class)
@Import(PscFilingConfig.class)
class ValidationStatusControllerImplFlagUndefinedIT extends BaseControllerIT {
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private FilingValidationService filingValidationService;
    @MockBean
    private PscMapper filingMapper;
    @MockBean
    private ErrorMapper errorMapper;
    @MockBean
    private Logger logger;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        baseSetUp();
    }

    @Test
    void validateWhenFeatureFlagIsUndefined() throws Exception {
        final var filing = PscIndividualFiling.builder()
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
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
        mockMvc.perform(get("/transactions/{transactionId}/persons-with-significant"
                        + "-control/{filingResourceId}/validation", TRANS_ID, FILING_ID)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }


}