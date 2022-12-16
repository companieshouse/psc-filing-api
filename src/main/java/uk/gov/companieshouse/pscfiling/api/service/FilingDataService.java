package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

/**
 * Produces Filing Data format for consumption as JSON by filing-resource-handler external service.
 */
public interface FilingDataService {


    /**
     * @param filingId          the PSC Filing id
     * @param transaction       the transaction for the filing
     * @param passthroughHeader the Http header
     * @return
     */
    FilingApi generatePscFiling(String filingId, Transaction transaction, String passthroughHeader);
}
