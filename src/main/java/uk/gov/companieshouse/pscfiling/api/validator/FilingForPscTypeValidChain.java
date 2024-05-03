package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Objects;
import java.util.StringJoiner;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public record FilingForPscTypeValidChain(PscTypeConstants pscType, FilingValid first) implements FilingForPscTypeValid {
    public FilingForPscTypeValidChain(final PscTypeConstants pscType,
                                      final FilingValid first) {
        this.pscType = Objects.requireNonNull(pscType);
        this.first = Objects.requireNonNull(first);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FilingForPscTypeValidChain.class.getSimpleName() + "[",
                "]").add("pscType=" + pscType).add("first=" + first).toString();
    }
}
