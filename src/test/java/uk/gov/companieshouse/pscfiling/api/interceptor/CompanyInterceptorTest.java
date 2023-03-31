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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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
    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    Logger logger;
    @Mock
    private Transaction transaction;

    private CompanyProfileApi companyProfileApi;
    private CompanyInterceptor testCompanyInterceptor;
    @Autowired
    @Qualifier(value = "validation")
    private Map<String, String> validation;
    @Autowired
    @Qualifier(value = "company")
    private  Map<String, List<String>> company;
    @Autowired
    @Qualifier(value = "companyStatus")
    private Map<String, String> companyStatus;
    @Autowired
    @Qualifier(value = "companyType")
    private Map<String, String> companyType;

    @BeforeEach
    void setUp() {
        companyProfileApi = new CompanyProfileApi();
        companyProfileApi.setHasSuperSecurePscs(Boolean.FALSE);
        companyProfileApi.setType("ltd");
        companyProfileApi.setCompanyStatus("active");

        testCompanyInterceptor =new CompanyInterceptor(companyProfileService, validation, company, companyStatus,
                companyType, logger);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
    }

    @Test
    void preHandleNoValidationErrors() {
        expectHeaderWithCompanyProfile();
        var result = testCompanyInterceptor.preHandle(request, response, handler);
        assertTrue(result);
    }

    @Test
    void preHandleWhenCompanyHasSuperSecurePscs() {
        expectHeaderWithCompanyProfile();
        companyProfileApi.setHasSuperSecurePscs(Boolean.TRUE);
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, "A PSC for this company has details protected. You can only file for this company on paper");
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyTypeNotAllowed() {
        expectHeaderWithCompanyProfile();
        companyProfileApi.setType("not-proper");
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, "This filing cannot be submitted for a " + companyType.get(companyProfileApi.getType()));
        List<FieldError> errors = List.of(error);

        final var thrown = assertThrows(ConflictingFilingException.class,
                () -> testCompanyInterceptor.preHandle(request, response, handler));

        assertThat(thrown.getFieldErrors(), is(errors));
    }

    @Test
    void preHandleWhenCompanyStatusNotAllowed() {
        expectHeaderWithCompanyProfile();
        companyProfileApi.setCompanyStatus("dissolved");
        final var error = new FieldError("object", "reference_psc_id",
                null, false, new String[]{null, "reference_psc_id"},
                null, "You cannot submit a filing for a company that is " + companyStatus.get(companyProfileApi.getCompanyStatus()));
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

