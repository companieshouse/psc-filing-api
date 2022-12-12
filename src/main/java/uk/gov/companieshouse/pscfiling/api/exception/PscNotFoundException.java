package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * PSC not found.
 */
public class PscNotFoundException extends RuntimeException {

    public PscNotFoundException(final String s, final Exception e) {
        super(s, e);
    }
}
