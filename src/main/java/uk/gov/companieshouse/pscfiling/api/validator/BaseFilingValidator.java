package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import java.util.Optional;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

public class BaseFilingValidator implements FilingPermissible {

    private FilingPermissible nextValidator;

    @Override
    public void validate(PscIndividualDto dto, List<FieldError> errors, Transaction transaction,
            PscTypeConstants pscType, String passthroughHeader) {

        Optional.ofNullable(nextValidator)
                .ifPresent(v -> v.validate(dto, errors, transaction, pscType, passthroughHeader));
    }

    @Override
    public void setNext(FilingPermissible filingValidator) {
        this.nextValidator = filingValidator;
    }
}
