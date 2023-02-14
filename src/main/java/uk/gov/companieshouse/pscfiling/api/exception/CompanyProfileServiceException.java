package uk.gov.companieshouse.pscfiling.api.exception;

/**
 * Company Profile not found or external query failed.
 */
public class CompanyProfileServiceException extends RuntimeException {
    public CompanyProfileServiceException(final String s, final Exception e) {
        super(s, e);
    }
}
