package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.validator.CeasedOnDateValidator;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValid;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValidChain;
import uk.gov.companieshouse.pscfiling.api.validator.PscEtagValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscIsActiveValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscRegisterEntryDateValidator;
import uk.gov.companieshouse.pscfiling.api.validator.TerminationRequiredFieldsValidator;

@Configuration
public class ValidatorConfig {

    @Bean
    public FilingForPscTypeValid filingForIndividualValid(final TerminationRequiredFieldsValidator terminationRequiredFieldsValidator,
                                                          final PscExistsValidator pscExistsValidator,
                                                          final PscEtagValidator pscEtagValidator,
                                                          final CeasedOnDateValidator ceasedOnDateValidator,
                                                          final PscRegisterEntryDateValidator pscRegisterEntryDateValidator,
                                                          final PscIsActiveValidator pscIsActiveValidator) {
        createValidationChain(terminationRequiredFieldsValidator, pscExistsValidator, pscEtagValidator, ceasedOnDateValidator,
                pscRegisterEntryDateValidator, pscIsActiveValidator);

        return new FilingForPscTypeValidChain(PscTypeConstants.INDIVIDUAL, terminationRequiredFieldsValidator);
    }

    @Bean
    public FilingForPscTypeValid filingForCorporateEntityValid(final TerminationRequiredFieldsValidator terminationRequiredFieldsValidator,
                                                          final PscExistsValidator pscExistsValidator,
                                                          final PscEtagValidator pscEtagValidator,
                                                          final CeasedOnDateValidator ceasedOnDateValidator,
                                                          final PscRegisterEntryDateValidator pscRegisterEntryDateValidator,
                                                          final PscIsActiveValidator pscIsActiveValidator) {
        createValidationChain(terminationRequiredFieldsValidator, pscExistsValidator, pscEtagValidator,
                ceasedOnDateValidator, pscRegisterEntryDateValidator, pscIsActiveValidator);

        return new FilingForPscTypeValidChain(PscTypeConstants.CORPORATE_ENTITY, terminationRequiredFieldsValidator);
    }

    @Bean
    public FilingForPscTypeValid filingForLegalPersonValid(final TerminationRequiredFieldsValidator terminationRequiredFieldsValidator,
                                                           final PscExistsValidator pscExistsValidator,
                                                           final PscEtagValidator pscEtagValidator,
                                                           final CeasedOnDateValidator ceasedOnDateValidator,
                                                           final PscRegisterEntryDateValidator pscRegisterEntryDateValidator,
                                                           final PscIsActiveValidator pscIsActiveValidator) {
        createValidationChain(terminationRequiredFieldsValidator, pscExistsValidator, pscEtagValidator,
                ceasedOnDateValidator, pscRegisterEntryDateValidator, pscIsActiveValidator);

        return new FilingForPscTypeValidChain(PscTypeConstants.LEGAL_PERSON, terminationRequiredFieldsValidator);
    }

    private static void createValidationChain(TerminationRequiredFieldsValidator terminationRequiredFieldsValidator,
                                              PscExistsValidator pscExistsValidator, PscEtagValidator pscEtagValidator,
                                              CeasedOnDateValidator ceasedOnDateValidator,
                                              PscRegisterEntryDateValidator pscRegisterEntryDateValidator,
                                              PscIsActiveValidator pscIsActiveValidator) {
        terminationRequiredFieldsValidator.setNext(pscExistsValidator);
        pscExistsValidator.setNext(pscEtagValidator);
        pscEtagValidator.setNext(ceasedOnDateValidator);
        ceasedOnDateValidator.setNext(pscRegisterEntryDateValidator);
        pscRegisterEntryDateValidator.setNext(pscIsActiveValidator);
    }

}
