package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Optional;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

public class BaseFilingValidator implements FilingValid {

    private FilingValid nextValidator;

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        Optional.ofNullable(nextValidator).ifPresent(v -> v.validate(validationContext));
    }

    @Override
    public void setNext(final FilingValid filingValidator) {
        this.nextValidator = filingValidator;
    }
}
