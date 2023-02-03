package uk.gov.companieshouse.pscfiling.api.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor.COMPANY_HAS_SUPER_SECURE_PSCS_MESSAGE;
import static uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor.COMPANY_STATUS_NOT_ALLOWED_MESSAGE;
import static uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor.COMPANY_TYPE_NOT_ALLLOWED_MESSSAGE;

import java.util.List;
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
    private Transaction transaction;

    private CompanyProfileApi companyProfileApi;
    private CompanyInterceptor testCompanyInterceptor;

    @BeforeEach
    void setUp() {
        testCompanyInterceptor = new CompanyInterceptor(companyProfileService);
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setHasSuperSecurePscs(Boolean.FALSE);
        companyProfileApi.setType("ltd");
        companyProfileApi.setCompanyStatus("active");
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        when(companyProfileService.getCompanyProfile(transaction, PASSTHROUGH_HEADER)).thenReturn(companyProfileApi);

    }

    @Test
    void preHandleNoValidationErrors() {
        var result = testCompanyInterceptor.preHandle(request, response, handler);
        assertTrue(result);
    }

    @Test
    void preHandleWhenCompanyHasSuperSecurePscs() {
        companyProfileApi.setHasSuperSecurePscs(Boolean.TRUE);
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, COMPANY_HAS_SUPER_SECURE_PSCS_MESSAGE);
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyTypeNotAllowed() {
        companyProfileApi.setType("not-proper");
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, COMPANY_TYPE_NOT_ALLLOWED_MESSSAGE + companyProfileApi.getType());
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyStatusNotAllowed() {
        companyProfileApi.setCompanyStatus("dissolved");
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, COMPANY_STATUS_NOT_ALLOWED_MESSAGE + companyProfileApi.getCompanyStatus());
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyProfileNull() {
        when(companyProfileService.getCompanyProfile(transaction, PASSTHROUGH_HEADER)).thenReturn(null);
        var result = testCompanyInterceptor.preHandle(request, response, handler);
        assertTrue(result);
    }
}

