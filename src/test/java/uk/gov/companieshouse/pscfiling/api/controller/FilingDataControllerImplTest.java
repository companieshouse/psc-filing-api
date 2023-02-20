package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@ExtendWith(MockitoExtension.class)
class FilingDataControllerImplTest extends BaseControllerIT {
    @Mock
    private FilingDataService filingDataService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private Logger logger;
    @Mock
    private HttpServletRequest request;

    private FilingDataControllerImpl testController;
    private Transaction filingsTransaction;

    @BeforeEach
    void setUp() throws Exception {
        testController =
                new FilingDataControllerImpl(filingDataService, transactionService, logger);
        filingsTransaction = new Transaction();
        filingsTransaction.setId(TRANS_ID);
        filingsTransaction.setCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void getFilingsData() {
        var filingApi = new FilingApi();
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);
        when(filingDataService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                filingsTransaction, PASSTHROUGH_HEADER)).thenReturn(filingApi);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                filingsTransaction);

        final var filingsList =
                testController.getFilingsData(TRANS_ID, PscTypeConstants.INDIVIDUAL, FILING_ID,
                        request);

        assertThat(filingsList, Matchers.contains(filingApi));
    }

    @Test
    void getFilingsDataWhenNotFound() {
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                filingsTransaction);
        when(filingDataService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                filingsTransaction, PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("Test Resource not found"));

        final var exception = assertThrows(FilingResourceNotFoundException.class,
                () -> testController.getFilingsData(TRANS_ID, PscTypeConstants.INDIVIDUAL,
                        FILING_ID, request));
        assertThat(exception.getMessage(), is("Test Resource not found"));
    }
}