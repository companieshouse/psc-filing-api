package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor;
import uk.gov.companieshouse.pscfiling.api.interceptor.RequestLoggingInterceptor;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    private InterceptorConfig testConfig;

    @Mock
    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    @Mock
    private CompanyInterceptor companyInterceptor;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InterceptorRegistry interceptorRegistry;
    @Mock
    private PermissionsMapping permissionsMapping;
    @Mock
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @BeforeEach
    void setUp() {
        testConfig = new InterceptorConfig();
    }

    @Test
    void addInterceptors() {
        testConfig.setTokenPermissionsInterceptor(tokenPermissionsInterceptor);
        testConfig.setCompanyInterceptor(companyInterceptor);
        testConfig.setRequestLoggingInterceptor(requestLoggingInterceptor);
        testConfig.addInterceptors(interceptorRegistry);

        verify(interceptorRegistry.addInterceptor(any(TransactionInterceptor.class)))
                .order(1);
        verify(interceptorRegistry.addInterceptor(any(OpenTransactionInterceptor.class))
                .addPathPatterns(
                        "/transactions/{transaction_id}/persons-with-significant-control/{pscType:"
                                + "(?:individual|corporate-entity|legal-person)}")).order(2);
        verify(interceptorRegistry.addInterceptor(companyInterceptor)).order(3);
        verify(interceptorRegistry.addInterceptor(tokenPermissionsInterceptor)).order(4);
        verify(interceptorRegistry.addInterceptor(any(MappablePermissionsInterceptor.class)))
                .order(5);
        verify(interceptorRegistry.addInterceptor(any(ClosedTransactionInterceptor.class))
                .addPathPatterns("/private"
                        + "/transactions/{transaction_id}/persons-with-significant-control"
                        + "/{pscType:"
                        + "(?:individual|corporate-entity|legal-person)}"
                        + "/{filing_resource_id}/filings")).order(6);
        verify(interceptorRegistry.addInterceptor(requestLoggingInterceptor)).order(7);
    }

    @Test
    void openTransactionInterceptor() {
        assertThat(testConfig.openTransactionInterceptor(), isA(OpenTransactionInterceptor.class));
    }

    @Test
    void closedTransactionInterceptor() {
        assertThat(testConfig.transactionClosedInterceptor(), isA(ClosedTransactionInterceptor.class));
    }

    @Test
    void testTransactionInterceptor() {
        assertThat(testConfig.transactionInterceptor(), isA(TransactionInterceptor.class));
    }

    @Test
    void requestPermissionsInterceptor() {
        assertThat(testConfig.requestPermissionsInterceptor(permissionsMapping),
                isA(MappablePermissionsInterceptor.class));
    }

    @Test
    void pscPermissionsMapping() {
        final var mapping = testConfig.pscPermissionsMapping();

        assertThat(mapping.apply(HttpMethod.POST.toString()),
                containsInAnyOrder(Permission.Value.DELETE));
        assertThat(mapping.apply(HttpMethod.GET.toString()),
                containsInAnyOrder(Permission.Value.DELETE));
        assertThat(mapping.apply(HttpMethod.PATCH.toString()),
                containsInAnyOrder(Permission.Value.DELETE));
    }
}