package uk.gov.companieshouse.pscfiling.api.config.enumerations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.gov.companieshouse.pscfiling.api.enumerations.YamlPropertySourceFactory;

@Configuration
@PropertySource(value = "classpath:api-enumerations/psc_filing.yml", factory = YamlPropertySourceFactory.class)
public class PscFilingConfig {

    @Bean("validation")
    @ConfigurationProperties(prefix = "validation")
    public Map<String, String> validation() {
        return new HashMap<>();
    }

    @Bean("company")
    @ConfigurationProperties(prefix = "company")
    public  Map<String, List<String>> company() {
        return new HashMap<>();
    }
}
