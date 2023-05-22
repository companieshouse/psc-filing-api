package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * A problem occurred when merging the PATCH and an IOException was thrown
 */
public class MergePatchException extends RuntimeException {

    public MergePatchException(final Throwable cause) {
        super(cause);
    }

}
