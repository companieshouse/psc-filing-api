package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.util.security.Permission;
import uk.gov.companieshouse.pscfiling.api.interceptor.CompanyInterceptor;
import uk.gov.companieshouse.pscfiling.api.interceptor.RequestLoggingInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.api")
public class InterceptorConfig implements WebMvcConfigurer {

    public static final String COMMON_INTERCEPTOR_PATH =
            "/transactions/{transaction_id}/persons-with-significant-control/{pscType:"
                    + "(?:individual|corporate-entity|legal-person)}";
    public static final String FILINGS_PATH =
            "/private" + COMMON_INTERCEPTOR_PATH + "/{filing_resource_id}/filings";
    private static final String PSC_FILING_API = "psc-filing-api";

    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    private CompanyInterceptor companyInterceptor;
    private InternalUserInterceptor internalUserInterceptor;
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @Autowired
    public void setTokenPermissionsInterceptor(
            final TokenPermissionsInterceptor tokenPermissionsInterceptor) {
        this.tokenPermissionsInterceptor = tokenPermissionsInterceptor;
    }

    @Autowired
    public void setCompanyInterceptor(final CompanyInterceptor companyInterceptor) {
        this.companyInterceptor = companyInterceptor;
    }

    @Autowired
    public void setInternalUserInterceptor(InternalUserInterceptor internalUserInterceptor) {
        this.internalUserInterceptor = internalUserInterceptor;
    }

    @Autowired
    public void setRequestLoggingInterceptor(final RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
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
        addInternalUserInterceptor(registry);
        addRequestLoggingInterceptor(registry);
    }

    private void addTransactionInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor())
                .order(1);
    }

    private void addOpenTransactionInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(openTransactionInterceptor())
                .addPathPatterns(COMMON_INTERCEPTOR_PATH).order(2);
    }

    private void addCompanyInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(companyInterceptor).order(3);
    }

    private void addTokenPermissionsInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenPermissionsInterceptor).order(4);

    }

    private void addRequestPermissionsInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(requestPermissionsInterceptor(pscPermissionsMapping()))
                .order(5);
    }

    private void addInternalUserInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(internalUserInterceptor)
                .addPathPatterns(FILINGS_PATH).order(6);
    }

    private void addTransactionClosedInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(transactionClosedInterceptor())
                .addPathPatterns(FILINGS_PATH).order(7);
    }

    private void addRequestLoggingInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor).order(8);
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
        return new ClosedTransactionInterceptor(FILINGS_PATH);
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
                .defaultRequireAnyOf(Permission.Value.DELETE)
                .build();
    }
}