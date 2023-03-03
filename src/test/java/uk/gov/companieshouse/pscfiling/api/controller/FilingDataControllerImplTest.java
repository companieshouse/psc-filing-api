package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants.INDIVIDUAL;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.BadInternalStateException;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@ExtendWith(MockitoExtension.class)
class FilingDataControllerImplTest extends BaseControllerIT {
    @Mock
    private TransactionService transactionService;
    @Mock
    private FilingDataService filingDataService;
    @Mock
    private Logger logger;
    @Mock
    private HttpServletRequest request;
    @Mock
    private PscFilingService pscFilingService;
    private FilingDataControllerImpl testController;
    private Transaction filingsTransaction;
    private PscCommunal filing;
    private static final String SELF_FRAGMENT =
            "/transactions/" + TRANS_ID + "/persons-with-significant-control/";

    @BeforeEach
    void setUp() throws Exception {
        testController = new FilingDataControllerImpl(pscFilingService, transactionService,
                filingDataService, logger);
        filingsTransaction = new Transaction();
        filingsTransaction.setId(TRANS_ID);
        filingsTransaction.setCompanyNumber(COMPANY_NUMBER);

        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment(PscTypeConstants.INDIVIDUAL.getValue())
                .pathSegment(FILING_ID)
                .build()
                .toUri();

        final Links links = new Links(self, null);
        filing = PscIndividualFiling.builder().links(links)
                .build();
    }

    @Test
    void getFilingsData() {
        var filingApi = new FilingApi();
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);

        when(filingDataService.generatePscFiling(FILING_ID, INDIVIDUAL, filingsTransaction,
                PASSTHROUGH_HEADER)).thenReturn(filingApi);

        when(filingDataService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                filingsTransaction, PASSTHROUGH_HEADER)).thenReturn(filingApi);

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var filingsList =
                testController.getFilingsData(TRANS_ID, FILING_ID, filingsTransaction, request);

        assertThat(filingsList, Matchers.contains(filingApi));
    }

    @Test
    void getFilingsDataWhenTransactionNullOriginal() {
        var filingApi = new FilingApi();
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                filingsTransaction);

        when(filingDataService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                filingsTransaction, PASSTHROUGH_HEADER)).thenReturn(filingApi);

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var filingsList = testController.getFilingsData(TRANS_ID, FILING_ID, null, request);

        assertThat(filingsList, Matchers.contains(filingApi));
    }

    @Test
    void getFilingsDataWhenNotFound() {
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);

        final var exception = assertThrows(FilingResourceNotFoundException.class,
                () -> testController.getFilingsData(TRANS_ID, FILING_ID, filingsTransaction,
                        request));
        assertThat(exception.getMessage(), is("Filing resource not found: " + FILING_ID));
    }

    @Test
    void getFilingsDataWithUnknownType() {
        filing = getFilingWithCorruptLinks();

        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(
                PASSTHROUGH_HEADER);

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        final var exception = assertThrows(BadInternalStateException.class,
                () -> testController.getFilingsData(TRANS_ID, FILING_ID, filingsTransaction,
                        request));
        assertThat(exception.getMessage(), is("PSC type not supported for PSC ID " + FILING_ID));

    }

    private PscCommunal getFilingWithCorruptLinks() {
        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment("UNKNOWN")
                .pathSegment(FILING_ID)
                .build()
                .toUri();
        final Links links = new Links(self, null);
        filing = PscIndividualFiling.builder().links(links)
                .build();

        return filing;
    }

}