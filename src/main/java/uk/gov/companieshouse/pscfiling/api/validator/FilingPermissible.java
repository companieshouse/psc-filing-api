package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

public interface FilingPermissible {
    /**
     * @param dto the DTO to validate
     * @param errors the list of errors append; MUST be non-null and modifiable
     * @param transaction the Transaction
     * @param pscType the PSC type
     * @param passthroughHeader the request passthrough header
     */
    void validate(PscIndividualDto dto, List<FieldError> errors, Transaction transaction,
            PscTypeConstants pscType, String passthroughHeader);

    void setNext(FilingPermissible filingValidator);
}
