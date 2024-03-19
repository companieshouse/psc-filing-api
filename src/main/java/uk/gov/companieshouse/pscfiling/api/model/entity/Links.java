package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Links object.
 */
public record Links(URI self, URI validationStatus) {
    public static final String PREFIX_PRIVATE = "/private";

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var links = (Links) o;
        return Objects.equals(self(), links.self()) && Objects.equals(validationStatus(),
                links.validationStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(self(), validationStatus());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Links.class.getSimpleName() + "[", "]").add("self=" + self)
                .add("validationStatus=" + validationStatus)
                .toString();
    }
}
