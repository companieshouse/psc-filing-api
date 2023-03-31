package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

/**
 * A previous validator must have checked that the PSC exists.
 */
@Component
public class PscEtagValidator extends BaseFilingValidator
        implements FilingValid {

    private final PscDetailsService pscDetailsService;

    public PscEtagValidator(PscDetailsService pscDetailsService, Map<String, String> validation) {
        super(validation);
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final PscApi pscDetails = pscDetailsService.getPscDetails(validationContext.getTransaction(),
                validationContext.getDto().getReferencePscId(), validationContext.getPscType(),
                validationContext.getPassthroughHeader());

        if (!StringUtils.equals(pscDetails.getEtag(), validationContext.getDto().getReferenceEtag())) {
            validationContext.getErrors()
                    .add(new FieldError("object", "reference_etag", validationContext.getDto().getReferenceEtag(),
                            false, new String[]{null, "notMatch.reference_etag"}, null,
                            validation.get("etag-not-match")));
        }

        super.validate(validationContext);
    }
}