package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapperImpl;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapperImpl;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    public PscMapper pscIndividualMapper() {
        return new PscMapperImpl();
    }

    @Bean
    public ErrorMapper errorMapper() {
        return new ErrorMapperImpl();
    }
}
