package uk.gov.companieshouse.pscfiling.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Main application configuration class.
 */
@Configuration
public class AppConfig {
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

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder().serializationInclusion(
                JsonInclude.Include.NON_NULL)
            .simpleDateFormat("yyyy-MM-dd")
            .failOnUnknownProperties(true) // override Spring Boot default (false)
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Bean("postObjectMapper")
    @Primary
    public ObjectMapper objectMapper() {
        return objectMapperBuilder().build();
    }

    @Bean
    @Qualifier("patchObjectMapper")
    public ObjectMapper patchObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    }

}
