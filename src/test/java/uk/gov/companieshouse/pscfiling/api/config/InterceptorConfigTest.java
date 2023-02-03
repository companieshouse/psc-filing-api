package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
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
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    private InterceptorConfig testConfig;
    @Mock
    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    @Mock
    private CompanyInterceptor companyInterceptor;
    @Mock
    private InterceptorRegistry interceptorRegistry;
    @Mock
    private InterceptorRegistration interceptorRegistration;

    @BeforeEach
    void setUp() {
        testConfig = new InterceptorConfig();
    }

    @Test
    void addInterceptors() {
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(any(TransactionInterceptor.class));
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(any(OpenTransactionInterceptor.class));
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(companyInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(tokenPermissionsInterceptor);

        testConfig.setTokenPermissionsInterceptor(tokenPermissionsInterceptor);
        testConfig.setCompanyInterceptor(companyInterceptor);
        testConfig.addInterceptors(interceptorRegistry);

        InOrder inOrder = Mockito.inOrder(interceptorRegistry);

        inOrder.verify(interceptorRegistry).addInterceptor(any(TransactionInterceptor.class));
        inOrder.verify(interceptorRegistry).addInterceptor(any(OpenTransactionInterceptor.class));
        inOrder.verify(interceptorRegistry).addInterceptor(companyInterceptor);
        inOrder.verify(interceptorRegistry).addInterceptor(tokenPermissionsInterceptor);
        verify(interceptorRegistration, times(4)).addPathPatterns("/transactions/**");
    }


    @Test
    void openTransactionInterceptor() {
        assertThat(testConfig.openTransactionInterceptor(), isA(OpenTransactionInterceptor.class));
    }

    @Test
    void testTransactionInterceptor() {
        assertThat(testConfig.transactionInterceptor(), isA(TransactionInterceptor.class));
    }
}

