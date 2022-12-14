package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

/**
 * Interacts with the external CHS PSC API service to retrieve PSCs.
 */
public interface PscDetailsService {
    /**
     * Retrieve a PSC by ID.
     *
     * @param transaction       the Transaction
     * @param pscId             the PSC Id
     * @param pscType           the PSC Type
     * @param ericPassThroughHeader includes authorisation for transaction fetch
     * @return the PSC details if found
     * @throws PscServiceException if PSC details not found or an error occurred
     */
    PscApi getPscDetails(Transaction transaction, String pscId, PscTypeConstants pscType, final String ericPassThroughHeader)
            throws PscServiceException;
}
