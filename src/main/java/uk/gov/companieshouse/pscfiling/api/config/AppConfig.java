package uk.gov.companieshouse.pscfiling.api.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;

/**
 * Main application configuration class.
 */
@Configuration
public class AppConfig {
    public AppConfig() {
        // required no-arg constructor
    }

    /**
     * Obtains a clock that returns the current instant, converting to date and time using the
     * UTC time-zone. Singleton bean provides consistent UTC timestamps.
     *
     * @return a Clock that uses the best available system clock in the UTC zone, not null
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean(name = "dtoFiling")
    public FilingValidator dtoFilingValidator(PscDetailsService pscDetailsService) {

        var firstValidator = new PscExistsValidator(pscDetailsService);

        firstValidator.setNext(new PscExistsValidator(pscDetailsService));

        return firstValidator;
    }
}
