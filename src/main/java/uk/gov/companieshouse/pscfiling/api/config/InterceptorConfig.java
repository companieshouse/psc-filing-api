package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.api")
public class InterceptorConfig implements WebMvcConfigurer {

    static final String[] TRANSACTIONS_LIST = {"/transactions/**"};
    public static final String PSC_FILING_API = "psc-filing-api";

    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    private CompanyInterceptor companyInterceptor;

    @Autowired
    public void setTokenPermissionsInterceptor(TokenPermissionsInterceptor tokenPermissionsInterceptor) {
        this.tokenPermissionsInterceptor = tokenPermissionsInterceptor;
    }

    @Autowired
    public void setCompanyInterceptor(CompanyInterceptor companyInterceptor) {
        this.companyInterceptor = companyInterceptor;
    }

    /**
     * Set up the interceptors to run against endpoints when the endpoints are called
     * Interceptors are executed in order of configuration
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        addTransactionInterceptor(registry);
        addOpenTransactionInterceptor(registry);
        addCompanyInterceptor(registry);
        addTokenPermissionsInterceptor(registry);
    }

    private void addTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor())
                .addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addOpenTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(openTransactionInterceptor())
                .addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addCompanyInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(companyInterceptor).addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addTokenPermissionsInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(tokenPermissionsInterceptor).addPathPatterns(TRANSACTIONS_LIST);

    }

    @Bean
    public OpenTransactionInterceptor openTransactionInterceptor() {
        return new OpenTransactionInterceptor(PSC_FILING_API);
    }

    @Bean
    public TransactionInterceptor transactionInterceptor() {
        return new TransactionInterceptor(PSC_FILING_API);
    }

}