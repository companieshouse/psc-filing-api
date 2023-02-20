package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

public class FilingValidationContext <T extends PscDtoCommunal> {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FilingValidationContext<?> that = (FilingValidationContext<?>) o;
        return Objects.equals(getErrors(), that.getErrors()) && Objects.equals(getTransaction(),
                that.getTransaction()) && getPscType() == that.getPscType() && Objects.equals(
                getPassthroughHeader(), that.getPassthroughHeader()) && Objects.equals(getDto(),
                that.getDto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrors(), getTransaction(), getPscType(), getPassthroughHeader(),
                getDto());
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
}