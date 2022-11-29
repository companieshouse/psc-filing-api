package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.psc.PscsResourceHandler;
import uk.gov.companieshouse.api.handler.psc.request.PscIndividualGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.PSCServiceException;

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
    @ValueSource(strings = {"individual", "corporate-entity", "legal-person"})
    void getPscDetailsWhenFound(String pscType) throws IOException, URIValidationException {
        when(apiResponse.getData()).thenReturn(new PscApi());
        when(pscIndividualGet.execute()).thenReturn(apiResponse);
        when(pscsResourceHandler.getIndividual("/company/" +
                COMPANY_NUMBER +
                "/persons-with-significant-control/" +
                pscType +
                "/" +
                PSC_ID)).thenReturn(pscIndividualGet);
        when(apiClient.pscs()).thenReturn(pscsResourceHandler);
        when(apiClientService.getOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        var pscApi = testService.getPscDetails(transaction, PSC_ID, pscType, PASSTHROUGH_HEADER);

        assertThat(pscApi, samePropertyValuesAs(new PscApi()));
    }

    @Test
    void getPscDetailsWhenNotFound() throws IOException, URIValidationException {
        when(apiClientService.getOauthAuthenticatedClient(PASSTHROUGH_HEADER)).thenThrow(IOException.class);

        assertThrows(PSCServiceException.class,
                () -> testService.getPscDetails(transaction, PSC_ID, "individual", PASSTHROUGH_HEADER));
    }
}

