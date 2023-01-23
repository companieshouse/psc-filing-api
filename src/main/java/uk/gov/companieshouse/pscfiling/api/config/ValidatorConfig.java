package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.validator.CeasedOnDateValidator;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValid;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValidChain;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;

@Configuration
public class ValidatorConfig {

    @Bean
    public FilingForPscTypeValid filingForIndividualValid(
            final PscExistsValidator pscExistsValidator, CeasedOnDateValidator ceasedOnDateValidator) {
        pscExistsValidator.setNext(ceasedOnDateValidator);
        return new FilingForPscTypeValidChain(PscTypeConstants.INDIVIDUAL, pscExistsValidator);
    }

}
