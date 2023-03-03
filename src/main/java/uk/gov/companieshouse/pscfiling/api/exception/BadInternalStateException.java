package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * Resource is in an inconsistent state e.g. self link contains unrecognised PSC type
 */
public class BadInternalStateException extends RuntimeException {

    public BadInternalStateException(final String s, final Exception e) {
        super(s, e);
    }

    public BadInternalStateException(final String s) {
        super(s);
    }

}