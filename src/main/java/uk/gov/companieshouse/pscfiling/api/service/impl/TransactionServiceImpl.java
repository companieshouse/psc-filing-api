package uk.gov.companieshouse.pscfiling.api.service.impl;

import static uk.gov.companieshouse.pscfiling.api.model.entity.Links.PREFIX_PRIVATE;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import java.io.IOException;
import java.text.MessageFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.sdk.ApiClientService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.TransactionServiceException;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final String UNEXPECTED_STATUS_CODE = "Unexpected Status Code received";
    private final ApiClientService apiClientService;
    private final Logger logger;

    public TransactionServiceImpl(final ApiClientService apiClientService, Logger logger) {
        this.apiClientService = apiClientService;
        this.logger = logger;
    }

    /**
     * Query the transaction service for a given transaction.
     *
     * @param transactionId         the Transaction ID
     * @param ericPassThroughHeader includes authorisation for the transaction query
     * @return the transaction if found
     *
     * @throws TransactionServiceException if not found or an error occurred
     */
    @Override
    public Transaction getTransaction(final String transactionId,
            final String ericPassThroughHeader) throws TransactionServiceException {
        final var logMap = LogHelper.createLogMap(transactionId);

        try {
            final var uri = "/transactions/" + transactionId;
            final var transaction =
                    apiClientService.getApiClient(ericPassThroughHeader)
                            .transactions()
                            .get(uri)
                            .execute()
                            .getData();
            logMap.put("company_number", transaction.getCompanyNumber());
            logMap.put("company_name", transaction.getCompanyName());
            logger.debugContext(transactionId, "Retrieved transaction details", logMap);
            return transaction;
        }
        catch (final ApiErrorResponseException e) {
            logger.errorContext(transactionId, UNEXPECTED_STATUS_CODE, e, logMap);
            throw new TransactionServiceException(
                    MessageFormat.format("Error Updating Transaction details for {0}: {1} {2}",
                            transactionId, e.getStatusCode(), e.getStatusMessage()), e);
        }
        catch (final URIValidationException | IOException e) {
            throw new TransactionServiceException("Error Retrieving Transaction " + transactionId,
                    e);
        }
    }

    /**
     * Update a given transaction via the transaction service.
     *
     * @param transaction           the Transaction ID
     * @throws TransactionServiceException if the transaction update failed
     */
    @Override
    public void updateTransaction(final Transaction transaction)
            throws TransactionServiceException {
        final var logMap = LogHelper.createLogMap(transaction.getId());
        try {
            logger.debugContext(transaction.getId(), "Updating transaction", logMap);
            final var uri = PREFIX_PRIVATE + "/transactions/" + transaction.getId();
            final var resp =
                    apiClientService.getInternalApiClient()
                            .privateTransaction()
                            .patch(uri, transaction)
                            .execute();

            if (HttpStatus.NO_CONTENT.value() != resp.getStatusCode()) {
                throw new ApiErrorResponseException(
                        new HttpResponseException.Builder(resp.getStatusCode(),
                                UNEXPECTED_STATUS_CODE, new HttpHeaders()));
            }
        }
        catch (final ApiErrorResponseException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            throw new TransactionServiceException(
                    MessageFormat.format("Error Updating Transaction details for {0}: {1} {2}",
                            transaction.getId(), e.getStatusCode(), e.getStatusMessage()), e);

        }
        catch (final URIValidationException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            throw new TransactionServiceException(
                    MessageFormat.format("Error Updating Transaction {0}: {1}", transaction.getId(),
                            e.getMessage()), e);
        }
    }

}
