package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

@Component
public class PscRegisterEntryDateValidator extends BaseFilingValidator
        implements FilingValid {

    public PscRegisterEntryDateValidator(Map<String, String> validation) {
        super(validation);
    }

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final var registerEntryDate = validationContext.getDto().getRegisterEntryDate();
        final var ceasedOnDate = validationContext.getDto().getCeasedOn();

        if (registerEntryDate.isBefore(ceasedOnDate)) {

            validationContext.getErrors()
                    .add(new FieldError("object", "register_entry_date", registerEntryDate, false,
                            new String[]{null, "date.register_entry_date"}, null,
                            validation.get("register-date-before-ceased-date")));
        }

        super.validate(validationContext);
    }
}