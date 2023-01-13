package uk.gov.companieshouse.pscfiling.api.validator;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

@Component
public class PscCeasedOnNotBeforeLegislationDateValidator extends BaseFilingValidator
        implements FilingPermissible {

    private static final LocalDate PSC_LEGISLATION_DATE = LocalDate.of(2016, Month.APRIL, 6);

    @Override
    public void validate(final PscIndividualDto dto, final List<FieldError> errors,
            final Transaction transaction, final PscTypeConstants pscType,
            final String passThroughHeader) {

        if (!PSC_LEGISLATION_DATE.isBefore(dto.getCeasedOn())) {
            errors.add(new FieldError("object", "ceased_on", dto.getCeasedOn(), false,
                    new String[]{null, "notBeforeLegislationDate.ceased_on"}, null,
                    MessageFormat.format("Date must not be before legislation date {0}",
                            PSC_LEGISLATION_DATE)));
        }

        super.validate(dto, errors, transaction, pscType, passThroughHeader);
    }

}