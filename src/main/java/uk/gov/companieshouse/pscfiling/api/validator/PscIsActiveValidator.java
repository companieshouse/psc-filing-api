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

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final PscApi pscDetails;
        pscDetails = pscDetailsService.getPscDetails(validationContext.getTransaction(),
                validationContext.getDto().getReferencePscId(), validationContext.getPscType(),
            validationContext.getPassthroughHeader());

        if (Optional.ofNullable(pscDetails.getCeasedOn()).isPresent()) {
            validationContext.getErrors()
                .add(new FieldError("object", "ceased_on", validationContext.getDto().getCeasedOn(),
                    false, new String[]{null, "date.ceased_on"}, null,
                    validation.get("psc-is-ceased")));
        }

        super.validate(validationContext);
    }
}
