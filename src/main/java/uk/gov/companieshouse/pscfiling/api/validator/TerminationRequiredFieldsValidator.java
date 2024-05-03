package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

@Component
public class TerminationRequiredFieldsValidator extends BaseFilingValidator
        implements FilingValid {

    protected static final String OBJECT_NAME = "object";

    public TerminationRequiredFieldsValidator(Map<String, String> validation) {
        super(validation);
    }

    /**
     * Validates that all required fields are present before a termination can take place.
     * @param validationContext     the validation context
     */
    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        if (validationContext.dto().getCeasedOn() == null) {
            validationContext.errors()
                    .add(new FieldError(OBJECT_NAME, "ceased_on", null, false,
                            new String[]{null, "ceased_on"},
                            null, validation.get("ceased-date-missing")));
        }
        if (validationContext.dto().getRegisterEntryDate() == null) {
            validationContext.errors()
                    .add(new FieldError(OBJECT_NAME, "register_entry_date", null, false,
                            new String[]{null, "register_entry_date"},
                            null, validation.get("register-date-missing")));
        }
        if (validationContext.dto().getReferencePscId() == null || validationContext.dto().getReferencePscId().isEmpty()) {
            validationContext.errors()
                    .add(new FieldError(
                            OBJECT_NAME, "reference_psc_id", null, false,
                            new String[]{null, "reference_psc_id"},
                            null, validation.get("reference-psc-id-missing")));
        }
        if (validationContext.dto().getReferenceEtag() == null || validationContext.dto().getReferenceEtag().isEmpty()) {
            validationContext.errors()
                    .add(new FieldError(OBJECT_NAME, "reference_etag", null, false,
                            new String[]{null, "reference_etag"},
                            null, validation.get("reference-etag-missing")));
        }

        // Only if required fields are present, should 'business' validation proceed
        if (validationContext.errors().isEmpty()) {
            super.validate(validationContext);
        }
    }

}
