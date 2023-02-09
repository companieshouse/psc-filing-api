package uk.gov.companieshouse.pscfiling.api.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.AttributeName;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.service.CompanyProfileService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@SpringBootTest
class CompanyInterceptorTest {

    private static final String PASSTHROUGH_HEADER = "passthrough";
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Object handler;
    @MockBean
    private CompanyProfileService companyProfileService;
    @MockBean
    Logger logger;
    @Mock
    private Transaction transaction;

    private CompanyProfileApi companyProfileApi;
    @Autowired
    private CompanyInterceptor testCompanyInterceptor;

    @BeforeEach
    void setUp() {
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
                null, "As a result of protection under section 790ZG of the Companies Act 2006 this form cannot be " +
                "filed online. You can only file this form on paper, refer to the information pack that was sent on " +
                "application of the protection.");
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
                null, "PSC form cannot be filed for this company type: " + companyProfileApi.getType());
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
                null, "Form cannot be filed for a company status that is " + companyProfileApi.getCompanyStatus());
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

    @Test
    void preHandleWhenTransactionInRequestNull() {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(null);
        final var thrown = assertThrows(NullPointerException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));
        assertThat(thrown.getMessage(), is("Transaction missing from request"));
    }
}

