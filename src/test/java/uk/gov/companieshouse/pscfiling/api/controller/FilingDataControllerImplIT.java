package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("web")
@WebMvcTest(controllers = FilingDataControllerImpl.class)
class FilingDataControllerImplIT extends BaseControllerIT {
    @MockBean
    private FilingDataService filingDataService;
    @MockBean
    private PscIndividualFilingService pscIndividualFilingService;
    @MockBean
    private Logger logger;
    @MockBean
    private TransactionService transactionService;
    @Autowired
    private MockMvc mockMvc;

    protected static final String URL_PSC_FILINGS =
            "/private/transactions/{id}/persons-with-significant-control/individual/{filingId"
                    + "}/filings";

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void getFilingsWhenFound() throws Exception {
        final var filingApi = new FilingApi();
        filingApi.setKind(FilingKind.PSC_CESSATION.getValue());
        final Map<String, Object> dataMap =
                Map.of("referenceEtag", ETAG, "referencePscId", PSC_ID, "filingResourceId",
                        CEASED_ON, "registerEntryDate", REGISTER_ENTRY);
        filingApi.setData(dataMap);

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(filingDataService.generatePscFiling(FILING_ID, transaction,
                PASSTHROUGH_HEADER)).thenReturn(filingApi);

        mockMvc.perform(get(URL_PSC_FILINGS, TRANS_ID, FILING_ID).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].data", is(dataMap)))
                .andExpect(jsonPath("$[0].kind", is(FilingKind.PSC_CESSATION.getValue())));
    }

    @Test
    void getFilingsWhenNotFound() throws Exception {
        when(filingDataService.generatePscFiling(FILING_ID, transaction,
                PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("for Not Found scenario", null));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);

        mockMvc.perform(get(URL_PSC_FILINGS, TRANS_ID, FILING_ID).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is("Resource not found")))
                .andExpect(jsonPath("$").doesNotExist());
    }
}