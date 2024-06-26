package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscIsActiveValidator extends BaseFilingValidator {

    private final PscDetailsService pscDetailsService;

    public PscIsActiveValidator(PscDetailsService pscDetailsService, Map<String, String> validation) {
        super(validation);
        this.pscDetailsService = pscDetailsService;
    }

    /**
     * Validates that the psc entity is in an active state.
     * @param validationContext     the validation context
     */
    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final PscApi pscDetails;
        pscDetails = pscDetailsService.getPscDetails(validationContext.transaction(),
                validationContext.dto().getReferencePscId(), validationContext.pscType(),
            validationContext.passthroughHeader());

        if (Optional.ofNullable(pscDetails.getCeasedOn()).isPresent()) {
            validationContext.errors()
                .add(new FieldError("object", "ceased_on", validationContext.dto().getCeasedOn(),
                    false, new String[]{null, "date.ceased_on"}, null,
                    validation.get("psc-is-ceased")));
        }

        super.validate(validationContext);
    }
}
