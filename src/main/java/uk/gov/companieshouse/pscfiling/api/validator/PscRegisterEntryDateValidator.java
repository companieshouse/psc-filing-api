package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

@Component
public class PscRegisterEntryDateValidator extends BaseIndividualFilingValidator
        implements IndividualFilingValid {

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final var registerEntryDate = validationContext.getDto().getRegisterEntryDate();
        final var ceasedOnDate = validationContext.getDto().getCeasedOn();

        if (registerEntryDate.isBefore(ceasedOnDate)) {

            validationContext.getErrors()
                    .add(new FieldError("object", "register_entry_date", registerEntryDate, false,
                            new String[]{null, "date.register_entry_date"}, null,
                            "PSC register entry date cannot be before the cessation date"));
        }

        super.validate(validationContext);
    }
}