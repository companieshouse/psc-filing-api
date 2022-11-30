package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * Transaction not found or external query failed.
 */
public class TransactionServiceException extends RuntimeException {

    public TransactionServiceException(final String s, final Exception e) {
        super(s, e);
    }
}
