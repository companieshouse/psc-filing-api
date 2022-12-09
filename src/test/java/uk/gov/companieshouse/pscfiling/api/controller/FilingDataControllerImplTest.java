package uk.gov.companieshouse.pscfiling.api.controller;

import javax.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingDataControllerImplTest {

    public static final String TRANS_ID = "117524-754816-491724";
    public static final String FILING_ID = "6332aa6ed28ad2333c3a520a";

    @Mock
    private PscIndividualFiling pscIndividualFiling;

    @Mock
    private FilingDataService filingDataService;
    @Mock
    private TransactionService transactionService;

    @Mock
    private Logger logger;

    @Mock
    private HttpServletRequest request;

    private FilingDataControllerImpl testController;

    @BeforeEach
    void setUp() {
        testController = new FilingDataControllerImpl(filingDataService, transactionService, logger);
    }

//    @Test
//    void getFilingsData() {
//        var filingApi = new FilingApi();
//        when(filingDataService.generatePscFiling(TRANS_ID, FILING_ID)).thenReturn(filingApi);
//        final var filingsList= testController.getFilingsData(TRANS_ID, FILING_ID, request);
//
//        assertThat(filingsList, Matchers.contains(filingApi));
//    }
//
//    @Test
//    void getFilingsDataWhenNotFound() {
//
//        when(filingDataService.generatePscFiling(TRANS_ID, FILING_ID)).thenThrow(new FilingResourceNotFoundException("Test Resource not found"));
//
//        final var exception = assertThrows(FilingResourceNotFoundException.class,
//                () -> testController.getFilingsData(TRANS_ID, FILING_ID, request));
//        assertThat(exception.getMessage(), is("Test Resource not found"));
//    }
}