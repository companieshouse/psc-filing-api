package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValid;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValidChain;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscIsActiveValidator;

@Configuration
public class ValidatorConfig {

    @Bean
    public FilingForPscTypeValid filingForIndividualValid(
        final PscExistsValidator pscExistsValidator, PscIsActiveValidator pscIsActiveValidator) {
        pscExistsValidator.setNext(pscIsActiveValidator);
        return new FilingForPscTypeValidChain(PscTypeConstants.INDIVIDUAL, pscExistsValidator);
    }

}
