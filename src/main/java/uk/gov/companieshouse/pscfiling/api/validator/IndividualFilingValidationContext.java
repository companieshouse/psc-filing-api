package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

public class IndividualFilingValidationContext extends AbstractFilingValidationContext {
    private final PscIndividualDto dto;

    /**
     * @param dto the DTO to validate
     * @param errors the list of errors append; MUST be non-null and modifiable
     * @param transaction the Transaction
     * @param pscType the PSC type
     * @param passthroughHeader the request passthrough header
     */
    public IndividualFilingValidationContext(final PscIndividualDto dto, final List<FieldError> errors,
            final Transaction transaction, final PscTypeConstants pscType,
            final String passthroughHeader) {
        super(errors, transaction, pscType, passthroughHeader);
        this.dto = Objects.requireNonNull(dto);
    }

    public PscIndividualDto getDto() {
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
        IndividualFilingValidationContext that = (IndividualFilingValidationContext) o;
        return getDto().equals(that.getDto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDto());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndividualFilingValidationContext.class.getSimpleName() + "[", "]").add(
                        "dto=" + dto)
                .add("errors=" + errors)
                .add("transaction=" + transaction)
                .add("pscType=" + pscType)
                .add("passthroughHeader='" + passthroughHeader + "'")
                .toString();
    }
}
