package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Optional;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ApiErrors;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

public class FilingValidatorImpl implements FilingValidator {

    private FilingValidator nextValidator;

    @Override
    public ApiErrors validate(PscIndividualDto dto, ApiErrors errors, Transaction transaction,
                              PscTypeConstants pscType, String passthroughHeader) {

        var apiErrors = Optional.ofNullable(errors).orElseGet(ApiErrors::new);

        if (nextValidator != null) {

            apiErrors =
                nextValidator.validate(dto, apiErrors, transaction, pscType, passthroughHeader);
        }

        return apiErrors;
    }

    @Override
    public void setNext(FilingValidator filingValidator) {

        this.nextValidator = filingValidator;
    }
}
