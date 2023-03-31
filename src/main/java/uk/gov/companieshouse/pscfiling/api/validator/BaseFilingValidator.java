package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

public class BaseFilingValidator implements FilingValid {

    protected Map<String, String> validation;
    private FilingValid nextValidator;

    @Autowired
    public BaseFilingValidator(@Qualifier(value = "validation") Map<String, String> validation) {
        this.validation = validation;
    }

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        Optional.ofNullable(nextValidator).ifPresent(v -> v.validate(validationContext));
    }

    @Override
    public void setNext(final FilingValid filingValidator) {
        this.nextValidator = filingValidator;
    }
}
