package uk.gov.companieshouse.pscfiling.api.model;

import java.util.EnumSet;
import java.util.Optional;

public enum FilingKind {

    PSC_CESSATION("psc-filing#cessation",  "Notice of ceasing to be a Person of Significant Control");

    FilingKind(final String value, String description) {
        this.value = value;
        this.description = description;
    }

    private final String value;
    private final String description;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<FilingKind> nameOf(final String value) {
        return EnumSet.allOf(FilingKind.class).stream().filter(v -> v.getValue().equals(value)).findAny();
    }
}
