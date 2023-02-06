package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscExistsValidator extends BaseIndividualFilingValidator
        implements IndividualFilingValid {

    private final PscDetailsService pscDetailsService;

    public PscExistsValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public void validate(final IndividualFilingValidationContext validationContext) {

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
                            new String[]{null, "notFound.reference_psc_id"}, null, "PSC with that reference ID was not found"));
        }
    }

}
