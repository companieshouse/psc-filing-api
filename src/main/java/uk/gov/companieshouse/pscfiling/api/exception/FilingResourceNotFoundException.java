package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * PSC Filing resource not found.
 */
public class FilingResourceNotFoundException extends RuntimeException {

    public FilingResourceNotFoundException(final String message) {
        super(message);
    }
}
