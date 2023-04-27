package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
public interface FilingDataService {

    /**
     * @param filingId          the PSC Filing id
     * @param pscType           the PSC type
     * @param transaction       the transaction for the filing
     * @param passthroughHeader the Http header
     * @return the filing data details
     */
    FilingApi generatePscFiling(String filingId, final PscTypeConstants pscType,
            Transaction transaction, String passthroughHeader);
}
