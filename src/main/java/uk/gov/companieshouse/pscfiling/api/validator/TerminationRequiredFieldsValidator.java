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

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        if (validationContext.getDto().getCeasedOn() == null) {
            validationContext.getErrors()
                    .add(new FieldError(OBJECT_NAME, "ceased_on", null, false,
                            new String[]{null, "ceased_on"},
                            null, validation.get("ceased-date-missing")));
        }
        if (validationContext.getDto().getRegisterEntryDate() == null) {
            validationContext.getErrors()
                    .add(new FieldError(OBJECT_NAME, "register_entry_date", null, false,
                            new String[]{null, "register_entry_date"},
                            null, validation.get("register-date-missing")));
        }
        if (validationContext.getDto().getReferencePscId() == null || validationContext.getDto().getReferencePscId().isEmpty()) {
            validationContext.getErrors()
                    .add(new FieldError(
                            OBJECT_NAME, "reference_psc_id", null, false,
                            new String[]{null, "reference_psc_id"},
                            null, validation.get("reference-psc-id-missing")));
        }
        if (validationContext.getDto().getReferenceEtag() == null || validationContext.getDto().getReferenceEtag().isEmpty()) {
            validationContext.getErrors()
                    .add(new FieldError(OBJECT_NAME, "reference_etag", null, false,
                            new String[]{null, "reference_etag"},
                            null, validation.get("reference-etag-missing")));
        }

        // Only if required fields are present, should 'business' validation proceed
        if (validationContext.getErrors().isEmpty()) {
            super.validate(validationContext);
        }
    }

}
