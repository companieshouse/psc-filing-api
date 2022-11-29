package uk.gov.companieshouse.pscfiling.api.model;

import java.util.EnumSet;
import java.util.Optional;

public enum PscTypeConstants {
    INDIVIDUAL("individual"),
    CORPORATE_ENTITY("corporate-entity"), 
    LEGAL_PERSON("legal-person");

    PscTypeConstants(final String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static Optional<PscTypeConstants> nameOf(final String value) {
        return EnumSet.allOf(PscTypeConstants.class).stream().filter(v -> v.getValue().equals(value)).findAny();
    }
}
