package uk.gov.companieshouse.pscfiling.api.config;

import static uk.gov.companieshouse.api.util.security.Permission.Key.USER_PROFILE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.pscfiling.api.interceptor.TestTransactionInterceptor;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Configuration
@ComponentScan("uk.gov.companieshouse.api")
@ComponentScan
public class InterceptorConfig implements WebMvcConfigurer {

    static final String TRANSACTIONS = "/transactions/**";
    static final String[] TRANSACTIONS_LIST = {"/transactions/**", "/private/**"};
    static final String FILINGS = "/private/**/filings";
    static final String[] USER_AUTH_ENDPOINTS = {};
    static final String[] INTERNAL_AUTH_ENDPOINTS = {
            FILINGS,
    };

//    @Autowired
//    private TransactionInterceptor transactionInterceptor;

//    @Autowired
//    private InternalUserInterceptor internalUserInterceptor;
//    @Autowired
//    private OpenTransactionInterceptor openTransactionInterceptor;

    @Autowired
    private TransactionService transactionService;

    /**
     * Set up the interceptors to run against endpoints when the endpoints are called
     * Interceptors are executed in order of configuration
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        addUserAuthenticationEndpointsInterceptor(registry);
//        addInternalUserAuthenticationEndpointsInterceptor(registry);
        addTestTransactionInterceptor(registry);
//        addTransactionInterceptor(registry);
        addOpenTransactionInterceptor(registry);
    }

    /**
     * Interceptor to authenticate access to specified endpoints using user permissions
     * @param registry
     */
    private void addUserAuthenticationEndpointsInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(getUserCrudAuthenticationInterceptor())
                .addPathPatterns(USER_AUTH_ENDPOINTS);
    }

    /**
     * Interceptor to authenticate access to specified endpoints using internal permissions
     * @param registry
     */
//    private void addInternalUserAuthenticationEndpointsInterceptor(InterceptorRegistry registry) {
//        registry.addInterceptor(internalUserInterceptor)
//                .addPathPatterns(INTERNAL_AUTH_ENDPOINTS);
//    }



    private void addTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor())
                .addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addTestTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(testTransactionInterceptor())
                .addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addOpenTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(openTransactionInterceptor())
                .addPathPatterns(TRANSACTIONS_LIST);
    }

    private CRUDAuthenticationInterceptor getUserCrudAuthenticationInterceptor() {
        return new CRUDAuthenticationInterceptor(USER_PROFILE);
    }

    public OpenTransactionInterceptor openTransactionInterceptor() {
        return new OpenTransactionInterceptor("psc-filing-api");
    }
    public TestTransactionInterceptor testTransactionInterceptor() {
        return new TestTransactionInterceptor(transactionService);
    }

    public TransactionInterceptor transactionInterceptor() {
        return new TransactionInterceptor();
    }

}


