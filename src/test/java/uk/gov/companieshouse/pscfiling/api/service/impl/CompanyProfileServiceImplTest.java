package uk.gov.companieshouse.pscfiling.api.service.impl;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.sdk.ApiClientService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.CompanyProfileServiceException;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceImplTest extends TestBaseService {

    private static final String URI = "/company/" + COMPANY_NUMBER;
    @Mock
    private ApiClient apiClient;
    @Mock
    private ApiClientService apiClientService;
    @Mock
    private CompanyGet companyGet;
    @Mock
    private CompanyResourceHandler companyResourceHandler;
    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;
    @Mock
    private Logger logger;
    @Mock
    private Transaction transaction;
    private CompanyProfileServiceImpl testService;

    @BeforeEach
    void setUp() {
        testService = new CompanyProfileServiceImpl(apiClientService, logger);
        when(transaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
    }

    @Test
    void getCompanyProfileWhenFound() throws IOException, URIValidationException {

        when(apiResponse.getData()).thenReturn(new CompanyProfileApi());
        when(companyGet.execute()).thenReturn(apiResponse);
        when(companyResourceHandler.get(URI)).thenReturn(companyGet);
        when(apiClient.company()).thenReturn(companyResourceHandler);
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);

        var companyProfileApi = testService.getCompanyProfile(transaction, PASSTHROUGH_HEADER);

        assertThat(companyProfileApi, samePropertyValuesAs(new CompanyProfileApi(), "hasSuperSecurePscs"));
    }

    @Test
    void getCompanyProfileWhenNotFound() throws IOException {
        final var exception = new ApiErrorResponseException(
                new HttpResponseException.Builder(HttpStatusCodes.STATUS_CODE_NOT_FOUND, "test case", new HttpHeaders()));
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenThrow(exception);

        final var thrown = assertThrows(CompanyProfileServiceException.class,
                () -> testService.getCompanyProfile(transaction, PASSTHROUGH_HEADER));

        assertThat(thrown.getMessage(), is("Error Retrieving company profile 12345678"));
    }
}