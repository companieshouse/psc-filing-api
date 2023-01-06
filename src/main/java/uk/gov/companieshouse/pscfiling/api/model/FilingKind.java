package uk.gov.companieshouse.pscfiling.api.model;

import java.util.EnumSet;
import java.util.Optional;

public enum FilingKind {

    PSC_CESSATION("psc-filing#cessation");

    FilingKind(final String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static Optional<FilingKind> nameOf(final String value) {
        return EnumSet.allOf(FilingKind.class).stream().filter(v -> v.getValue().equals(value)).findAny();
    }
}
