package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Optional;

public class BaseIndividualFilingValidator implements IndividualFilingValid {

    private IndividualFilingValid nextValidator;

    @Override
    public void validate(final IndividualFilingValidationContext validationContext) {

        Optional.ofNullable(nextValidator).ifPresent(v -> v.validate(validationContext));
    }

    @Override
    public void setNext(final IndividualFilingValid individualFilingValidator) {
        this.nextValidator = individualFilingValidator;
    }
}
