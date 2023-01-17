package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.psc.PscsResourceHandler;
import uk.gov.companieshouse.api.handler.psc.request.PscIndividualGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.sdk.ApiClientService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

@ExtendWith(MockitoExtension.class)
class PscDetailsServiceImplTest {

    public static final String PASSTHROUGH_HEADER = "passthrough";
    public static final String COMPANY_NUMBER = "012345678";
    public static final String PSC_ID = "654321";
    @Mock
    private ApiClientService apiClientService;
    @Mock
    private ApiClient apiClient;
    @Mock
    private ApiResponse<PscApi> apiResponse;
    @Mock
    private PscIndividualGet pscIndividualGet;
    @Mock
    private PscsResourceHandler pscsResourceHandler;
    @Mock
    private Transaction transaction;
    @Mock
    private Logger logger;
    private PscDetailsService testService;

    @BeforeEach
    void setUp() {
        testService = new PscDetailsServiceImpl(apiClientService, logger);
    }

    @ParameterizedTest
    @EnumSource(PscTypeConstants.class)
    void getPscDetailsWhenFound(final PscTypeConstants pscType)
            throws IOException, URIValidationException {
        when(apiResponse.getData()).thenReturn(new PscApi());
        when(pscIndividualGet.execute()).thenReturn(apiResponse);
        when(pscsResourceHandler.getIndividual("/company/" +
                COMPANY_NUMBER +
                "/persons-with-significant-control/" +
                pscType.getValue() +
                "/" +
                PSC_ID)).thenReturn(pscIndividualGet);
        when(apiClient.pscs()).thenReturn(pscsResourceHandler);
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(
                apiClient);
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        var pscApi = testService.getPscDetails(transaction, PSC_ID, pscType, PASSTHROUGH_HEADER);

        assertThat(pscApi, samePropertyValuesAs(new PscApi()));
    }

    @Test
    void getPscDetailsWhenIoException() throws IOException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(
                new IOException("get test case"));

        final var thrown = assertThrows(PscServiceException.class,
                () -> testService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                        PASSTHROUGH_HEADER));
        assertThat(thrown.getMessage(), is("Error Retrieving PSC details for 654321: get test case"));
    }

    @Test
    void getPscDetailsWhenNotFound() throws IOException {
        final var exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(HttpStatusCodes.STATUS_CODE_NOT_FOUND, "test case", new HttpHeaders()));
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(exception);

        final var thrown = assertThrows(FilingResourceNotFoundException.class,
                () -> testService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL, PASSTHROUGH_HEADER));

        assertThat(thrown.getMessage(), is("PSC Details not found for " + PSC_ID + ": 404 test case"));
    }

    @Test
    void getPscDetailsWhenErrorRetrieving() throws IOException {
        final var exception = new ApiErrorResponseException(
            new HttpResponseException.Builder(HttpStatusCodes.STATUS_CODE_FORBIDDEN, "test case", new HttpHeaders()));
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(exception);

        final var thrown = assertThrows(PscServiceException.class,
            () -> testService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL, PASSTHROUGH_HEADER));

        assertThat(thrown.getMessage(), is("Error Retrieving PSC details for " + PSC_ID + ": 403 test case"));
    }

}

