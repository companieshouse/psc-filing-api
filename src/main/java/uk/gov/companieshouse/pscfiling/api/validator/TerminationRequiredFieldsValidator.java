package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

@Component
public class TerminationRequiredFieldsValidator extends BaseIndividualFilingValidator
        implements IndividualFilingValid {

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        if (validationContext.getDto().getCeasedOn() == null) {
            validationContext.getErrors()
                    .add(new FieldError("object", "ceased_on", null, false,
                            new String[]{null, "ceased_on"},
                            null,"must not be null"));
        }
        if (validationContext.getDto().getRegisterEntryDate() == null) {
            validationContext.getErrors()
                    .add(new FieldError("object", "register_entry_date", null, false,
                            new String[]{null, "register_entry_date"},
                            null,"must not be null"));
        }
        if (validationContext.getDto().getReferencePscId() == null) {
            validationContext.getErrors()
                    .add(new FieldError("object", "reference_psc_id", null, false, new String[]{null, "reference_psc_id"},
                            null, "must not be null"));
        }
        if (validationContext.getDto().getReferenceEtag() == null) {
            validationContext.getErrors()
                    .add(new FieldError("object", "reference_etag", null,
                            false, new String[]{null, "reference_etag"},
                            null, "must not be null"));
        }

        // Only if required fields are present, should 'business' validation proceed
        if (validationContext.getErrors().isEmpty()) {
            super.validate(validationContext);
        }
    }

}
