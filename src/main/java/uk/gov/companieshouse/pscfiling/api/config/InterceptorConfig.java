package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.MappablePermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.PermissionsMapping;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.api.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.util.security.Permission;

@Configuration
@ComponentScan("uk.gov.companieshouse.api")
public class InterceptorConfig implements WebMvcConfigurer {

    static final String[] TRANSACTIONS_LIST = {"/transactions/**"};
    public static final String PSC_FILING_API = "psc-filing-api";

    private TokenPermissionsInterceptor tokenPermissionsInterceptor;

    @Autowired
    public void setTokenPermissionsInterceptor(
            final TokenPermissionsInterceptor tokenPermissionsInterceptor) {
        this.tokenPermissionsInterceptor = tokenPermissionsInterceptor;
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
        addTokenPermissionsInterceptor(registry);
        addRequestPermissionsInterceptor(registry);
    }

    private void addTransactionInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(transactionInterceptor()).addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addOpenTransactionInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(openTransactionInterceptor()).addPathPatterns(TRANSACTIONS_LIST);
    }

    private void addTokenPermissionsInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenPermissionsInterceptor).addPathPatterns(TRANSACTIONS_LIST);

    }

    private void addRequestPermissionsInterceptor(final InterceptorRegistry registry) {
        registry.addInterceptor(requestPermissionsInterceptor(pscPermissionsMapping()))
                .addPathPatterns(TRANSACTIONS_LIST);
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
    public MappablePermissionsInterceptor requestPermissionsInterceptor(
            final PermissionsMapping permissionMapping) {
        return new MappablePermissionsInterceptor(Permission.Key.COMPANY_PSCS, true,
                permissionMapping);
    }

    @Bean
    public PermissionsMapping pscPermissionsMapping() {
        return PermissionsMapping.builder()
                .defaultAllOf(Permission.Value.READ)
                .mapAllOf(HttpMethod.POST.toString(), Permission.Value.DELETE)
                .build();
    }
}