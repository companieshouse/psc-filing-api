package uk.gov.companieshouse.pscfiling.api.validator;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ApiErrors;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

@Component
public class PscCeasedOnNotBeforeLegislationDateValidator extends FilingValidatorImpl implements FilingValidator  {

    private static final LocalDate PSC_LEGISLATION_DATE = LocalDate.of(2016, Month.APRIL, 6);

    @Override
    public ApiErrors validate(final PscIndividualDto dto, final ApiErrors errors,
            final Transaction transaction, final PscTypeConstants pscType,
            final String passThroughHeader) {

        var apiErrors = Optional.ofNullable(errors).orElseGet(ApiErrors::new);

        if (!PSC_LEGISLATION_DATE.isBefore(dto.getCeasedOn())) {
            apiErrors.add(new ApiError(
                    MessageFormat.format("Date {0} must not be before legislation date of {1}", dto.getCeasedOn(), PSC_LEGISLATION_DATE), "$.ceased_on",
                    LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType()));
        }

        return super.validate(dto, apiErrors, transaction, pscType, passThroughHeader);
    }

}