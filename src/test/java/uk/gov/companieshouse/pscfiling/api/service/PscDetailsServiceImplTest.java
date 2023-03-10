package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants.CORPORATE_ENTITY;
import static uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants.INDIVIDUAL;
import static uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants.LEGAL_PERSON;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.psc.PscsResourceHandler;
import uk.gov.companieshouse.api.handler.psc.request.PscCorporateEntityGet;
import uk.gov.companieshouse.api.handler.psc.request.PscIndividualGet;
import uk.gov.companieshouse.api.handler.psc.request.PscLegalPersonGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.sdk.ApiClientService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

@ExtendWith(MockitoExtension.class)
class PscDetailsServiceImplTest extends TestBaseService {

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
    private PscCorporateEntityGet pscCorporateEntityGet;
    @Mock
    private PscLegalPersonGet pscLegalPersonGet;
    @Mock
    private PscsResourceHandler pscsResourceHandler;
    @Mock
    private Transaction transaction;
    @Mock
    private Logger logger;
    private PscDetailsService testService;

    @BeforeAll
    public static void setUpForClass() {
        PscTypeConstants[] newEnumValues = addNewEnumValue(PscTypeConstants.class);
        myMockedEnum = mockStatic(PscTypeConstants.class);
        myMockedEnum.when(PscTypeConstants::values).thenReturn(newEnumValues);
        mockedValue = newEnumValues[newEnumValues.length - 1];
        when(mockedValue.name()).thenReturn("UNKNOWN");
    }

    @AfterAll
    public static void tearDownForClass() {
        myMockedEnum.close();
    }

    @BeforeEach
    void setUp() {
        testService = new PscDetailsServiceImpl(apiClientService, logger);
    }

    @Test
    void getPscIndividualDetailsWhenFound() throws IOException, URIValidationException {
        when(apiResponse.getData()).thenReturn(new PscApi());
        when(pscIndividualGet.execute()).thenReturn(apiResponse);
        when(pscsResourceHandler.getIndividual("/company/"
                + COMPANY_NUMBER
                + "/persons-with-significant-control/"
                + INDIVIDUAL.getValue()
                + "/"
                +
                PSC_ID)).thenReturn(pscIndividualGet);
        when(apiClient.pscs()).thenReturn(pscsResourceHandler);
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(
                apiClient);
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        var pscApi = testService.getPscDetails(transaction, PSC_ID, INDIVIDUAL, PASSTHROUGH_HEADER);

        assertThat(pscApi, samePropertyValuesAs(new PscApi()));
    }

    @Test
    void getPscCorporateDetailsWhenFound()
            throws IOException, URIValidationException {
        when(apiResponse.getData()).thenReturn(new PscApi());
        when(pscCorporateEntityGet.execute()).thenReturn(apiResponse);
        when(pscsResourceHandler.getCorporateEntity("/company/" +
                COMPANY_NUMBER +
                "/persons-with-significant-control/" +
                CORPORATE_ENTITY.getValue() +
                "/" +
                PSC_ID)).thenReturn(pscCorporateEntityGet);
        when(apiClient.pscs()).thenReturn(pscsResourceHandler);
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(
                apiClient);
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        var pscApi = testService.getPscDetails(transaction, PSC_ID, CORPORATE_ENTITY, PASSTHROUGH_HEADER);

        assertThat(pscApi, samePropertyValuesAs(new PscApi()));
    }

    @Test
    void getPscLegalPersonDetailsWhenFound()
            throws IOException, URIValidationException {
        when(apiResponse.getData()).thenReturn(new PscApi());
        when(pscLegalPersonGet.execute()).thenReturn(apiResponse);
        when(pscsResourceHandler.getLegalPerson("/company/" +
                COMPANY_NUMBER +
                "/persons-with-significant-control/" +
                LEGAL_PERSON.getValue() +
                "/" +
                PSC_ID)).thenReturn(pscLegalPersonGet);
        when(apiClient.pscs()).thenReturn(pscsResourceHandler);
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(
                apiClient);
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        var pscApi = testService.getPscDetails(transaction, PSC_ID, LEGAL_PERSON, PASSTHROUGH_HEADER);

        assertThat(pscApi, samePropertyValuesAs(new PscApi()));
    }

    @Test
    void getPscDetailsWhenIoException() throws IOException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(
                new IOException("get test case"));

        final var thrown = assertThrows(PscServiceException.class,
                () -> testService.getPscDetails(transaction, PSC_ID, INDIVIDUAL,
                        PASSTHROUGH_HEADER));
        assertThat(thrown.getMessage(),
                is("Error Retrieving PSC details for " + PSC_ID + ": get test case"));
    }

    @Test
    void getPscDetailsWhenNotFound() throws IOException {
        final var exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(HttpStatusCodes.STATUS_CODE_NOT_FOUND, "test case", new HttpHeaders()));
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(exception);

        final var thrown = assertThrows(FilingResourceNotFoundException.class,
                () -> testService.getPscDetails(transaction, PSC_ID, INDIVIDUAL, PASSTHROUGH_HEADER));

        assertThat(thrown.getMessage(), is("PSC Details not found for " + PSC_ID + ": 404 test case"));
    }

    @Test
    void getPscDetailsWhenErrorRetrieving() throws IOException {
        final var exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(HttpStatusCodes.STATUS_CODE_FORBIDDEN,
                        "test case", new HttpHeaders()));
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(exception);

        final var thrown = assertThrows(PscServiceException.class,
            () -> testService.getPscDetails(transaction, PSC_ID, INDIVIDUAL, PASSTHROUGH_HEADER));

        assertThat(thrown.getMessage(),
                is("Error Retrieving PSC details for " + PSC_ID + ": 403 test case"));
    }

    @Test
    void getPscDetailsWhenTypeNotRecognised() {

        final var thrown = assertThrows(UnsupportedOperationException.class,
                () -> testService.getPscDetails(transaction, PSC_ID, mockedValue,
                        PASSTHROUGH_HEADER));

        assertThat(thrown.getMessage(), is("PSC type UNKNOWN not supported for PSC ID " + PSC_ID));
    }

}