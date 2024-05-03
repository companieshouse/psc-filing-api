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
public class PscEtagValidator extends BaseFilingValidator implements FilingValid {

    private final PscDetailsService pscDetailsService;

    public PscEtagValidator(PscDetailsService pscDetailsService, Map<String, String> validation) {
        super(validation);
        this.pscDetailsService = pscDetailsService;
    }

    /**
     * Validates that psc details eTag matches the dto eTag
     * @param validationContext the validation context
     */
    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final PscApi pscDetails = pscDetailsService.getPscDetails(validationContext.transaction(),
                validationContext.dto().getReferencePscId(), validationContext.pscType(),
                validationContext.passthroughHeader());

        if (!StringUtils.equals(pscDetails.getEtag(), validationContext.dto().getReferenceEtag())) {
            validationContext.errors()
                    .add(new FieldError("object", "reference_etag", validationContext.dto().getReferenceEtag(),
                            false, new String[]{null, "notMatch.reference_etag"}, null,
                            validation.get("etag-not-match")));
        }

        super.validate(validationContext);
    }
}