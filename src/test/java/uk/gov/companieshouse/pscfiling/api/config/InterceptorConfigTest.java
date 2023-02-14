package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor;
import uk.gov.companieshouse.api.util.security.Permission;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    private InterceptorConfig testConfig;

    @Mock
    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    @Mock
    private TransactionInterceptor transactionInterceptor;
    @Mock
    private CompanyInterceptor companyInterceptor;
    @Mock
    private OpenTransactionInterceptor openTransactionInterceptor;
    @Mock
    private InterceptorRegistry interceptorRegistry;
    @Mock
    private InterceptorRegistration interceptorRegistration;
    @Mock
    private PermissionsMapping permissionsMapping;

    @BeforeEach
    void setUp() {
        testConfig = new InterceptorConfig();
    }

    @Test
    void addInterceptors() {
        doReturn(interceptorRegistration).when(interceptorRegistry)
                .addInterceptor(any(TransactionInterceptor.class));
        doReturn(interceptorRegistration).when(interceptorRegistry)
                .addInterceptor(any(OpenTransactionInterceptor.class));
        doReturn(interceptorRegistration).when(interceptorRegistry)
                .addInterceptor(companyInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry)
                .addInterceptor(tokenPermissionsInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry)
                .addInterceptor(any(MappablePermissionsInterceptor.class));

        testConfig.setTokenPermissionsInterceptor(tokenPermissionsInterceptor);
        testConfig.setCompanyInterceptor(companyInterceptor);
        testConfig.addInterceptors(interceptorRegistry);

        InOrder inOrder = Mockito.inOrder(interceptorRegistry);

        inOrder.verify(interceptorRegistry).addInterceptor(any(TransactionInterceptor.class));
        inOrder.verify(interceptorRegistry).addInterceptor(any(OpenTransactionInterceptor.class));
        inOrder.verify(interceptorRegistry).addInterceptor(companyInterceptor);
        inOrder.verify(interceptorRegistry).addInterceptor(tokenPermissionsInterceptor);
        inOrder.verify(interceptorRegistry)
                .addInterceptor(any(MappablePermissionsInterceptor.class));
        verify(interceptorRegistration, times(5))
                .addPathPatterns("/transactions/{transaction_id}/persons-with-significant-control/{pscType:(?:individual|corporate-entity|legal-person)}");
    }

    @Test
    void openTransactionInterceptor() {
        assertThat(testConfig.openTransactionInterceptor(), isA(OpenTransactionInterceptor.class));
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
                containsInAnyOrder(Permission.Value.READ));
        assertThat(mapping.apply(HttpMethod.PATCH.toString()),
                containsInAnyOrder(Permission.Value.READ));
    }
}

