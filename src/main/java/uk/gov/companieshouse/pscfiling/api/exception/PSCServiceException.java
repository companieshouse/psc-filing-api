package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * PSC not found or external query failed.
 */
public class PSCServiceException extends RuntimeException {

    public PSCServiceException(final String s, final Exception e) {
        super(s, e);
    }
}
