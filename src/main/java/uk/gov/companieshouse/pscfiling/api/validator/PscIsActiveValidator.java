package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscIsActiveValidator extends BaseFilingValidator implements FilingValid {

    private final PscDetailsService pscDetailsService;

    public PscIsActiveValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public void validate(final FilingValidationContext validationContext) {

        final PscApi pscDetails;
        pscDetails = pscDetailsService.getPscDetails(validationContext.getTransaction(),
            validationContext.getDto().getReferencePscId(), validationContext.getPscType(),
            validationContext.getPassthroughHeader());

        if (Optional.ofNullable(pscDetails.getCeasedOn()).isPresent()) {
            validationContext.getErrors()
                .add(new FieldError("object", "ceased_on", validationContext.getDto().getCeasedOn(),
                    false, new String[]{null, "date.ceased_on"}, null,
                    "PSC is not active as a ceased on date is present"));
        }

        super.validate(validationContext);
    }
}
