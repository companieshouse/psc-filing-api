package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * An exception in this PSC Filing service itself.
 */
public class PscFilingServiceException extends RuntimeException {

    public PscFilingServiceException(final String s, final Exception e) {
        super(s, e);
    }
}
