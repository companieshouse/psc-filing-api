package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscExistsValidator extends BaseFilingValidator
        implements FilingValid {

    private final PscDetailsService pscDetailsService;

    public PscExistsValidator(PscDetailsService pscDetailsService, Map<String, String> validation) {
        super(validation);
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        try {
            pscDetailsService.getPscDetails(validationContext.getTransaction(), validationContext.getDto()
                                .getReferencePscId(), validationContext.getPscType(),
                        validationContext.getPassthroughHeader());
            // Validation should not continue if PSC does not exist
            super.validate(validationContext);
        }
        catch (FilingResourceNotFoundException e) {
            validationContext.getErrors().add(
                    new FieldError("object", "reference_psc_id", validationContext.getDto().getReferencePscId(), false,
                            new String[]{null, "notFound.reference_psc_id"}, null, validation.get("psc-reference-id-not-found")));
        }
    }

}
