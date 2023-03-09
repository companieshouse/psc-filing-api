package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.TransactionServiceException;

/**
 * Interacts with the external Transactions service to retrieve and update Transactions.
 */
public interface TransactionService {
    /**
     * Retrieve a Transaction by ID.
     *
     * @param transactionId the Transaction ID
     * @param ericPassThroughHeader includes authorisation for transaction fetch
     * @return the Transaction if found
     *
     * @throws TransactionServiceException if Transaction not found or an error occurred
     */
    Transaction getTransaction(String transactionId, final String ericPassThroughHeader)
            throws TransactionServiceException;

    /**
     * Update a Transaction by ID.
     *
     * @param transaction the Transaction object to update
     *
     * @throws TransactionServiceException if Transaction not found or an error occurred
     */
    void updateTransaction(Transaction transaction)
            throws TransactionServiceException;
}
