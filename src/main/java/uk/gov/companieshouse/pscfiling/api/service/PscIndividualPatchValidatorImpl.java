package uk.gov.companieshouse.pscfiling.api.service;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.patch.model.ValidationResult;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@Component
public class PscIndividualPatchValidatorImpl implements PscIndividualPatchValidator {
    private Clock clock;

    @Autowired
    public PscIndividualPatchValidatorImpl(final Clock clock) {
        this.clock = clock;
    }

    @Override
    public ValidationResult validate(final PscIndividualFiling pscIndividualFiling) {
        final var today = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (pscIndividualFiling.getCeasedOn().isAfter(today)) {
            return new ValidationResult(
                    new FieldError("patch", "ceasedOn", pscIndividualFiling.getCeasedOn(), false,
                            new String[]{
                                    "ceased_on",
                                    "patch.ceased_on"}, null,
                                    "date cannot be in the future"));
        }
        return new ValidationResult();
    }

}
