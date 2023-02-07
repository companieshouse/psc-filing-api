package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

@Component
public class PscRegisterEntryDateValidator extends BaseIndividualFilingValidator
        implements IndividualFilingValid {

    @Override
    public void validate(final FilingValidationContext validationContext) {

        final var registerEntryDate = validationContext.getDto().getRegisterEntryDate();
        final var ceasedOnDate = validationContext.getDto().getCeasedOn();

        if (registerEntryDate != null && ceasedOnDate != null && registerEntryDate.isBefore(
                ceasedOnDate)) {

            validationContext.getErrors()
                    .add(new FieldError("object", "register_entry_date", registerEntryDate, false,
                            new String[]{null, "date.register_entry_date"}, null,
                            "PSC register entry date cannot be before the cessation date"));
        }

        super.validate(validationContext);
    }
}