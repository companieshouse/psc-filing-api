package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.PSCServiceException;

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
     * @throws PSCServiceException if PSC details not found or an error occurred
     */
    PscApi getPscDetails(Transaction transaction, String pscId, String pscType, final String ericPassThroughHeader)
            throws PSCServiceException;
}
