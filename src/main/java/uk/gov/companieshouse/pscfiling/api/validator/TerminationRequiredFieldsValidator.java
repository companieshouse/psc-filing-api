package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

@Component
public class TerminationRequiredFieldsValidator extends BaseFilingValidator
        implements FilingValid {

    protected static final String OBJECT_NAME = "object";
    protected static final String DEFAULT_MESSAGE = "must not be null";

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        if (validationContext.getDto().getCeasedOn() == null) {
            validationContext.getErrors()
                    .add(new FieldError(OBJECT_NAME, "ceased_on", null, false,
                            new String[]{null, "ceased_on"},
                            null, DEFAULT_MESSAGE));
        }
        if (validationContext.getDto().getRegisterEntryDate() == null) {
            validationContext.getErrors()
                    .add(new FieldError(OBJECT_NAME, "register_entry_date", null, false,
                            new String[]{null, "register_entry_date"},
                            null, DEFAULT_MESSAGE));
        }
        if (validationContext.getDto().getReferencePscId() == null) {
            validationContext.getErrors()
                    .add(new FieldError(
                            OBJECT_NAME, "reference_psc_id", null, false,
                            new String[]{null, "reference_psc_id"},
                            null, DEFAULT_MESSAGE));
        }
        if (validationContext.getDto().getReferenceEtag() == null) {
            validationContext.getErrors()
                    .add(new FieldError(OBJECT_NAME, "reference_etag", null, false,
                            new String[]{null, "reference_etag"},
                            null, DEFAULT_MESSAGE));
        }

        // Only if required fields are present, should 'business' validation proceed
        if (validationContext.getErrors().isEmpty()) {
            super.validate(validationContext);
        }
    }

}
