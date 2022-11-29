package uk.gov.companieshouse.pscfiling.api.error;

/**
 * Validation error location types.
 */
public enum LocationType {

    RESOURCE("resource"),
    REQUEST_BODY("request-body"),
    JSON_PATH("json-path");

    private final String value;

    LocationType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}