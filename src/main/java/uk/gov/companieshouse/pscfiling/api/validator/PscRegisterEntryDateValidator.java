package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class PscRegisterEntryDateValidator extends BaseFilingValidator implements FilingValid {

    @Override
    public void validate(final FilingValidationContext validationContext) {

        if (validationContext.getDto().getRegisterEntryDate().isBefore(validationContext.getDto().getCeasedOn())) {
            validationContext.getErrors()
                    .add(new FieldError("object", "register_entry_date",
                            validationContext.getDto().getRegisterEntryDate(), false,
                            new String[]{null, "date.register_entry_date"}, null,
                            "PSC register entry date cannot be before the cessation date"));
        }

        super.validate(validationContext);
    }
}