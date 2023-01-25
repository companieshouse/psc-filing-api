package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapperImpl;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapperImpl;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;

@TestConfiguration
public class IntegrationTestConfig {
    @Bean
    public PscExistsValidator pscExistsValidator(final PscDetailsService pscDetailsService) {
        return new PscExistsValidator(pscDetailsService);
    }

    @Bean
    public PscIndividualMapper pscIndividualMapper() {
        return new PscIndividualMapperImpl();
    }

    @Bean
    public ErrorMapper errorMapper() {
        return new ErrorMapperImpl();
    }
}
