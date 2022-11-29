package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * PSC Filing resource not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
