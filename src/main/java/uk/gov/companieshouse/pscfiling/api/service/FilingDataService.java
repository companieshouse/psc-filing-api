package uk.gov.companieshouse.pscfiling.api.service;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
public interface FilingDataService {

    /**
     * Create FilingApi data from a retrieved Officer Filing resource.
     *
     * @param transactionId the Transaction ID
     * @param filingId      the Officer Filing ID
     * @return the FilingApi resource
     */
    FilingApi generatePscFiling(String filingId, HttpServletRequest request,
                Transaction transaction, String passthroughHeader);
}
