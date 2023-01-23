package uk.gov.companieshouse.pscfiling.api.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

/**
 * A previous validator must have checked that the PSC exists
 */
@Component
public class PscEtagValidator extends BaseFilingValidator implements FilingValid {

    private PscDetailsService pscDetailsService;

    public PscEtagValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public void validate(final FilingValidationContext validationContext) {

        final PscApi pscDetails;
        pscDetails = pscDetailsService.getPscDetails(validationContext.getTransaction(),
                validationContext.getDto().getReferencePscId(), validationContext.getPscType(),
                validationContext.getPassthroughHeader());

        if (!StringUtils.equals(pscDetails.getEtag(), validationContext.getDto().getReferenceEtag())) {
            validationContext.getErrors()
                    .add(new FieldError("object", "reference_etag", validationContext.getDto().getReferenceEtag(),
                            false, new String[]{null, "notMatch.reference_etag"}, null,
                            "Etag for PSC does not match latest value"));
        }

        super.validate(validationContext);
    }
}