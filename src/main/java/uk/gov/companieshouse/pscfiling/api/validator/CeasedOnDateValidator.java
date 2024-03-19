package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class CeasedOnDateValidator extends BaseFilingValidator
        implements FilingValid {

    private final PscDetailsService pscDetailsService;

    public CeasedOnDateValidator(PscDetailsService pscDetailsService, Map<String, String> validation) {
        super(validation);
        this.pscDetailsService = pscDetailsService;
    }

    /**
     * Validates that the eased on date is after the notified on date.
     * @param validationContext     the validation context
     */
    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final PscApi pscDetails = pscDetailsService.getPscDetails(validationContext.transaction(),
                validationContext.dto().getReferencePscId(), validationContext.pscType(),
                validationContext.passthroughHeader());
        final var ceasedOn = validationContext.dto().getCeasedOn();
        final var notifiedOn = pscDetails.getNotifiedOn();

        if (notifiedOn != null && ceasedOn.isBefore(notifiedOn)) {
            validationContext.errors()
                    .add(new FieldError("object", "ceased_on", ceasedOn, false, new String[]{null, "date.ceased_on"},
                            null, validation.get("ceased-date-before-notified-date")));
        }
        super.validate(validationContext);
    }

}
