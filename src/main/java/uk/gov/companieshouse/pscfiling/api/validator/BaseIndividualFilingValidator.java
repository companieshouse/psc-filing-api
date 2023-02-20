package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Optional;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

public class BaseIndividualFilingValidator implements IndividualFilingValid {

    private IndividualFilingValid nextValidator;

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        Optional.ofNullable(nextValidator).ifPresent(v -> v.validate(validationContext));
    }

    @Override
    public void setNext(final IndividualFilingValid individualFilingValidator) {
        this.nextValidator = individualFilingValidator;
    }
}
