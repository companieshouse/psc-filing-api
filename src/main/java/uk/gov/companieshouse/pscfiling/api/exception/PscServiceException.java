package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * PSC external query failed.
 */
public class PscServiceException extends RuntimeException {

    public PscServiceException(final String s, final Exception e) {
        super(s, e);
    }
}
