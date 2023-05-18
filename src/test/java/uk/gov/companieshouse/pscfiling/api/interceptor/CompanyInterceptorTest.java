package uk.gov.companieshouse.pscfiling.api.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.AttributeName;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.service.CompanyProfileService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@ExtendWith(MockitoExtension.class)
class CompanyInterceptorTest {

    private static final String PASSTHROUGH_HEADER = "passthrough";
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Object handler;
    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    Logger logger;
    @Mock
    private Transaction transaction;
    @Mock
    private Map<String, String> validation;
    @Mock
    private  Map<String, List<String>> company;
    @Mock
    private Map<String, String> companyStatus;

    private CompanyProfileApi companyProfileApi;
    private CompanyInterceptor testCompanyInterceptor;

    @BeforeEach
    void setUp() {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setHasSuperSecurePscs(Boolean.FALSE);
        companyProfileApi.setType("ltd");
        companyProfileApi.setCompanyStatus("active");

        testCompanyInterceptor =new CompanyInterceptor(companyProfileService, validation, company, companyStatus, logger);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
    }

    @Test
    void preHandleNoValidationErrors() {
        expectHeaderWithCompanyProfile();
        when(company.get("type-allowed")).thenReturn(List.of("ltd"));
        when(company.get("status-not-allowed")).thenReturn(List.of("dissolved"));

        var result = testCompanyInterceptor.preHandle(request, response, handler);

        assertTrue(result);
    }

    @Test
    void preHandleWhenCompanyHasSuperSecurePscs() {
        expectHeaderWithCompanyProfile();
        companyProfileApi.setHasSuperSecurePscs(Boolean.TRUE);
        when(validation.get("super-secure-company")).thenReturn("Super secure default message");
        final var error = new FieldError("ignored", "ignored", "Super secure default message");
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyTypeNotAllowed() {
        expectHeaderWithCompanyProfile();
        companyProfileApi.setType("not-proper");
        when(company.get("type-allowed")).thenReturn(List.of("ltd"));
        when(validation.get("company-type-not-allowed")).thenReturn("Invalid type default message");
        final var error = new FieldError("ignored", "ignored", "Invalid type default message");
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyStatusNotAllowed() {
        expectHeaderWithCompanyProfile();
        companyProfileApi.setCompanyStatus("dissolved");
        when(company.get("type-allowed")).thenReturn(List.of("ltd"));
        when(company.get("status-not-allowed")).thenReturn(List.of("dissolved"));
        when(validation.get("company-status-not-allowed")).thenReturn(
                "Invalid status default message");
        final var error = new FieldError("ignored", "ignored",
                "Invalid status default message" + companyStatus.get(
                        companyProfileApi.getCompanyStatus()));
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyProfileNull() {
        expectHeaderWithCompanyProfile();
        when(companyProfileService.getCompanyProfile(transaction, PASSTHROUGH_HEADER)).thenReturn(null);
        var result = testCompanyInterceptor.preHandle(request, response, handler);
        assertTrue(result);
    }

    @Test
    void preHandleWhenTransactionInRequestNull() {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(null);
        final var thrown = assertThrows(NullPointerException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));
        assertThat(thrown.getMessage(), is("Transaction missing from request"));
    }

    private void expectHeaderWithCompanyProfile() {
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        when(companyProfileService.getCompanyProfile(transaction, PASSTHROUGH_HEADER)).thenReturn(companyProfileApi);
    }
}

