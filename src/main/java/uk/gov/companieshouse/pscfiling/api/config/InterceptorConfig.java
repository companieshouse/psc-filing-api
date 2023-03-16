package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.api")
@PropertySource("classpath:validation.properties")
public class InterceptorConfig implements WebMvcConfigurer {

    static final String[] INTERCEPTOR_PATHS_LIST = {
            "/transactions/{transaction_id}/persons-with-significant-control/{pscType:"
                    + "(?:individual|corporate-entity|legal-person)}"
    };
    private static final String GET_FILINGS_ENDPOINT =
            "/private/transactions/{transaction_id}/persons-with-significant-control/{pscType:"
                    + "(?:individual|corporate-entity|legal-person)}/{filing_resource_id}/filings";
    private static final String PSC_FILING_API = "psc-filing-api";

    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    private CompanyInterceptor companyInterceptor;

    @Autowired
    public void setTokenPermissionsInterceptor(
            final TokenPermissionsInterceptor tokenPermissionsInterceptor) {
        this.tokenPermissionsInterceptor = tokenPermissionsInterceptor;
    }

    @Autowired
    public void setCompanyInterceptor(CompanyInterceptor companyInterceptor) {
        this.companyInterceptor = companyInterceptor;
    }

    /**
     * Set up the interceptors to run against endpoints when the endpoints are called
     * Interceptors are executed in order of configuration
     *
     * @param registry The {@link InterceptorRegistry} to configure
     */
    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {
        addTransactionInterceptor(registry);
        addOpenTransactionInterceptor(registry);
        addCompanyInterceptor(registry);
        addTokenPermissionsInterceptor(registry);
        addRequestPermissionsInterceptor(registry);
        addTransactionClosedInterceptor(registry);
    }

    private void addTransactionClosedInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(transactionClosedInterceptor())
                .addPathPatterns(GET_FILINGS_ENDPOINT);
    }

    private void addTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor())
                .addPathPatterns(INTERCEPTOR_PATHS_LIST);
    }

    private void addOpenTransactionInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(openTransactionInterceptor())
                .addPathPatterns(INTERCEPTOR_PATHS_LIST);
    }

    private void addCompanyInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(companyInterceptor).addPathPatterns(INTERCEPTOR_PATHS_LIST);
    }

    private void addTokenPermissionsInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(tokenPermissionsInterceptor).addPathPatterns(INTERCEPTOR_PATHS_LIST);

    }

    private void addRequestPermissionsInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(requestPermissionsInterceptor(pscPermissionsMapping()))
                .addPathPatterns(INTERCEPTOR_PATHS_LIST);
    }

    @Bean
    public OpenTransactionInterceptor openTransactionInterceptor() {
        return new OpenTransactionInterceptor(PSC_FILING_API);
    }

    @Bean
    public TransactionInterceptor transactionInterceptor() {
        return new TransactionInterceptor(PSC_FILING_API);
    }

    @Bean
    public ClosedTransactionInterceptor transactionClosedInterceptor() {
        return new ClosedTransactionInterceptor(GET_FILINGS_ENDPOINT);
    }

    @Bean
    public MappablePermissionsInterceptor requestPermissionsInterceptor(
            final PermissionsMapping permissionMapping) {
        return new MappablePermissionsInterceptor(Permission.Key.COMPANY_PSCS, true,
                permissionMapping);
    }

    @Bean
    public PermissionsMapping pscPermissionsMapping() {
        return PermissionsMapping.builder()
                .defaultRequireAnyOf(Permission.Value.READ)
                .mappedRequireAnyOf(HttpMethod.POST.toString(), Permission.Value.DELETE)
                .build();
    }
}