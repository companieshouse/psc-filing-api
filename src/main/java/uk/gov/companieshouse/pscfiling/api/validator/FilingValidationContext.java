package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public class FilingValidationContext <T> {
    private final List<FieldError> errors;
    private final Transaction transaction;
    private final PscTypeConstants pscType;
    private final String passthroughHeader;
    private final T dto;

    /**
     * @param dto the DTO to validate
     * @param errors the list of errors append; MUST be non-null and modifiable
     * @param transaction the Transaction
     * @param pscType the PSC type
     * @param passthroughHeader the request passthrough header
     */
    public FilingValidationContext(final T dto, final List<FieldError> errors,
            final Transaction transaction, final PscTypeConstants pscType,
            final String passthroughHeader) {
        this.dto = Objects.requireNonNull(dto);
        this.errors = Objects.requireNonNull(errors);
        this.transaction = Objects.requireNonNull(transaction);
        this.pscType = Objects.requireNonNull(pscType);
        this.passthroughHeader = passthroughHeader;
    }

    public T getDto() {
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilingValidationContext that = (FilingValidationContext) o;
        return getDto().equals(that.getDto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDto());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FilingValidationContext.class.getSimpleName() + "[", "]").add(
                        "dto=" + dto)
                .add("errors=" + errors)
                .add("transaction=" + transaction)
                .add("pscType=" + pscType)
                .add("passthroughHeader='" + passthroughHeader + "'")
                .toString();
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
