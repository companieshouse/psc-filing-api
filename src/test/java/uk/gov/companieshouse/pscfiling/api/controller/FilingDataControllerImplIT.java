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
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.PscNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("web")
@WebMvcTest(controllers = FilingDataControllerImpl.class)
class FilingDataControllerImplIT {
    private static final String TRANS_ID = "4f56fdf78b357bfc";
    private static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    private static final String PASSTHROUGH_HEADER = "passthrough";
    private static final String REF_PSC_ID = "12345";
    private static final String REF_ETAG = "6789";
    private static final String CEASED_ON = "2022-10-05";
    private static final String REGISTER_ENTRY = "2022-10-05";
    @MockBean
    private FilingDataService filingDataService;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private Logger logger;
    @MockBean
    private TransactionService transactionService;
    private Transaction transaction;
    private HttpHeaders httpHeaders;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASSTHROUGH_HEADER);
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");
    }

    @Test
    void getFilingsWhenFound() throws Exception {
        final var filingApi = new FilingApi();
        filingApi.setKind(FilingKind.PSC_CESSATION.getValue());
        final Map<String, Object> dataMap =
                Map.of("referenceEtag", REF_ETAG, "referencePscId", REF_PSC_ID, "filingResourceId", CEASED_ON, "registerEntryDate", REGISTER_ENTRY);
        filingApi.setData(dataMap);

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(transaction);
        when(filingDataService.generatePscFiling(FILING_ID, transaction, PASSTHROUGH_HEADER)).thenReturn(filingApi);

        mockMvc.perform(get("/private/transactions/{id}/persons-with-significant-control/{filingId}/filings", TRANS_ID, FILING_ID)
            .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].data", is(dataMap)))
            .andExpect(jsonPath("$[0].kind", is(FilingKind.PSC_CESSATION.getValue())));
    }

    @Test
    void getFilingsWhenNotFound() throws Exception {
        when(filingDataService.generatePscFiling(FILING_ID, transaction, PASSTHROUGH_HEADER)).thenThrow(new PscNotFoundException("for Not Found scenario", null));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(transaction);

        mockMvc.perform(get("/private/transactions/{id}/persons-with-significant-control/{filingId}/filings", TRANS_ID, FILING_ID)
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is("PSC not found")))
                .andExpect(jsonPath("$").doesNotExist());
    }
}