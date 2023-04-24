package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Links object.
 */
public class Links {
    public static final String PREFIX_PRIVATE = "/private";
    private final URI self;
    private final URI validationStatus;

    public Links(final URI self, final URI validationStatus) {
        this.self = self;
        this.validationStatus = validationStatus;
    }

    /**
     * @return The psc self uri link
     */
    public URI getSelf() {
        return self;
    }

    /**
     * @return The psc validation status uri link.
     */
    public URI getValidationStatus() {
        return validationStatus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var links = (Links) o;
        return Objects.equals(getSelf(), links.getSelf()) && Objects.equals(getValidationStatus(),
                links.getValidationStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelf(), getValidationStatus());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Links.class.getSimpleName() + "[", "]").add("self=" + self)
                .add("validationStatus=" + validationStatus)
                .toString();
    }
}
