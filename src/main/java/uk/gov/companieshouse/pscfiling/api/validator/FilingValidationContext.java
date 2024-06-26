package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

public record FilingValidationContext<T extends PscDtoCommunal>(@NonNull T dto,
                                                                @NonNull List<FieldError> errors,
                                                                @NonNull Transaction transaction,
                                                                @NonNull PscTypeConstants pscType,
                                                                String passthroughHeader) {
    /**
     * @param dto               the DTO to validate
     * @param errors            the list of errors append; MUST be non-null and modifiable
     * @param transaction       the Transaction
     * @param pscType           the PSC type
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FilingValidationContext<?> that = (FilingValidationContext<?>) o;
        return Objects.equals(errors(), that.errors())
                && Objects.equals(transaction(), that.transaction())
                && pscType() == that.pscType()
                && Objects.equals(passthroughHeader(), that.passthroughHeader())
                && Objects.equals(dto(), that.dto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors(), transaction(), pscType(), passthroughHeader(), dto());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FilingValidationContext.class.getSimpleName() + "[", "]")
                .add("dto=" + dto)
                .add("errors=" + errors)
                .add("transaction=" + transaction)
                .add("pscType=" + pscType)
                .add("passthroughHeader='" + passthroughHeader + "'")
                .toString();
    }
}