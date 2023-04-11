package uk.gov.companieshouse.pscfiling.api.service;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.patch.model.ValidationResult;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Component
public class PscWithIdentificationPatchValidatorImpl implements PscWithIdentificationPatchValidator {
    private Clock clock;

    @Autowired
    public PscWithIdentificationPatchValidatorImpl(final Clock clock) {
        this.clock = clock;
    }

    //TODO - should also check register_entry_date is not in the future
    @Override
    public ValidationResult validate(final PscWithIdentificationFiling pscWithIdentificationFiling) {
        final var today = clock.instant().atZone(ZoneId.systemDefault()).toLocalDate();

        return Optional.ofNullable(pscWithIdentificationFiling)
                .map(PscWithIdentificationFiling::getCeasedOn)
                .filter(d -> d.isAfter(today)).map(d -> new ValidationResult(
                        new FieldError("patch", "ceasedOn", d,
                                false, new String[]{
                                "ceased_on", "patch.ceased_on"
                        }, null, "date cannot be in the future")))
                .orElseGet(ValidationResult::new);
    }

}