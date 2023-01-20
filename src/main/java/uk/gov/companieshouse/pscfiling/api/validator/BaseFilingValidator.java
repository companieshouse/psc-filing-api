package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Optional;

public class BaseFilingValidator implements FilingValid {

    private FilingValid nextValidator;

    @Override
    public void validate(final FilingValidationContext validationContext) {

        Optional.ofNullable(nextValidator).ifPresent(v -> v.validate(validationContext));
    }

    @Override
    public void setNext(final FilingValid filingValidator) {
        this.nextValidator = filingValidator;
    }
}
