package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class CeasedOnDateValidator extends BaseFilingValidator implements FilingValid {

    private final PscDetailsService pscDetailsService;

    public CeasedOnDateValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public void validate(final FilingValidationContext validationContext) {

        final PscApi pscDetails = pscDetailsService.getPscDetails(validationContext.getTransaction(),
                validationContext.getDto().getReferencePscId(), validationContext.getPscType(),
                validationContext.getPassthroughHeader());

        final var ceasedOn = validationContext.getDto().getCeasedOn();
        if (ceasedOn != null && ceasedOn.isBefore(pscDetails.getNotifiedOn())) {
            validationContext.getErrors()
                    .add(new FieldError("object", "ceased_on", ceasedOn, false, new String[]{null, "date.ceased_on"},
                            null, "Ceased on date is before the date the PSC was notified on"));
        }
        super.validate(validationContext);
    }

}
