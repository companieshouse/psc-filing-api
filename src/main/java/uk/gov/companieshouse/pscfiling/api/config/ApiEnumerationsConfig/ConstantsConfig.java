package uk.gov.companieshouse.pscfiling.api.config.ApiEnumerationsConfig;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.companieshouse.pscfiling.api.enumerations.YamlPropertySourceFactory;

@Configuration
@PropertySource(value = "classpath:api-enumerations/constants.yml", factory = YamlPropertySourceFactory.class)
public class ConstantsConfig {

    @Bean("companyStatus")
    @ConfigurationProperties(prefix = "company-status")
    public Map<String, String> companyStatus() {
        return new HashMap<>();
    }

    @Bean("companyType")
    @ConfigurationProperties(prefix = "company-type")
    public Map<String, String> companyType() {
        return new HashMap<>();
    }
}