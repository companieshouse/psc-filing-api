package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import java.util.Objects;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public abstract class AbstractFilingValidationContext {
    protected final List<FieldError> errors;
    protected final Transaction transaction;
    protected final PscTypeConstants pscType;
    protected final String passthroughHeader;

    public AbstractFilingValidationContext(final List<FieldError> errors,
                                           final Transaction transaction,
                                           final PscTypeConstants pscType,
                                           final String passthroughHeader) {
        this.errors = Objects.requireNonNull(errors);
        this.transaction = Objects.requireNonNull(transaction);
        this.pscType = Objects.requireNonNull(pscType);
        this.passthroughHeader = passthroughHeader;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public PscTypeConstants getPscType() {
        return pscType;
    }

    public String getPassthroughHeader() {
        return passthroughHeader;
    }

}
