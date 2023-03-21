package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.Objects;
import java.util.StringJoiner;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public class FilingForPscTypeValidChain implements FilingForPscTypeValid {
    private final PscTypeConstants pscType;
    private final FilingValid first;

    public FilingForPscTypeValidChain(final PscTypeConstants pscType,
            final FilingValid first) {
        this.pscType = Objects.requireNonNull(pscType);
        this.first = Objects.requireNonNull(first);
    }

    @Override
    public PscTypeConstants getPscType() {
        return pscType;
    }

    @Override
    public FilingValid getFirst() {
        return first;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FilingForPscTypeValidChain.class.getSimpleName() + "[",
                "]").add("pscType=" + pscType).add("first=" + first).toString();
    }
}
