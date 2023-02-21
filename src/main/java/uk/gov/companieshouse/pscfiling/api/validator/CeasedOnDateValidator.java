package uk.gov.companieshouse.pscfiling.api.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class CeasedOnDateValidator extends BaseIndividualFilingValidator
        implements IndividualFilingValid {

    private final PscDetailsService pscDetailsService;

    public CeasedOnDateValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext) {

        final PscApi pscDetails = pscDetailsService.getPscDetails(validationContext.getTransaction(),
                validationContext.getDto().getReferencePscId(), validationContext.getPscType(),
                validationContext.getPassthroughHeader());
        final var ceasedOn = validationContext.getDto().getCeasedOn();
        final var notifiedOn = pscDetails.getNotifiedOn();

        if (ceasedOn != null && notifiedOn != null && ceasedOn.isBefore(notifiedOn)) {
            validationContext.getErrors()
                    .add(new FieldError("object", "ceased_on", ceasedOn, false, new String[]{null, "date.ceased_on"},
                            null, "Ceased on date cannot be before the date the PSC was notified on"));
        }
        super.validate(validationContext);
    }

}
