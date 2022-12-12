package uk.gov.companieshouse.pscfiling.api.service;

import java.io.IOException;
import java.text.MessageFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.PscNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@Service
public class PscDetailsServiceImpl implements PscDetailsService {
    private static final String UNEXPECTED_STATUS_CODE = "Unexpected Status Code received";

    private final ApiClientService apiClientService;
    private final Logger logger;

    public PscDetailsServiceImpl(final ApiClientService apiClientService, Logger logger) {
        this.apiClientService = apiClientService;
        this.logger = logger;
    }

    /**
     * Retrieve a PSC by ID.
     *
     * @param transaction           the Transaction
     * @param pscId                 the PSC Id
     * @param pscType               the PSC Type
     * @param ericPassThroughHeader includes authorisation for transaction fetch
     * @return the PSC details if found
     *
     * @throws PscServiceException if PSC details not found or an error occurred
     */
    @Override
    public PscApi getPscDetails(final Transaction transaction, String pscId,
            PscTypeConstants pscType, final String ericPassThroughHeader)
            throws PscServiceException {
        final var logMap = LogHelper.createLogMap(transaction.getId());

        try {
            final var uri = "/company/"
                    + transaction.getCompanyNumber()
                    + "/persons-with-significant-control/"
                    + pscType.getValue()
                    + "/"
                    + pscId;
            return apiClientService.getOauthAuthenticatedClient(ericPassThroughHeader)
                    .pscs()
                    .getIndividual(uri)
                    .execute()
                    .getData();
        }
        catch (final ApiErrorResponseException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            if (e.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new PscNotFoundException(
                        MessageFormat.format("PSC Details not found for {0}: {1} {2}", pscId,
                                e.getStatusCode(), e.getStatusMessage()), e);
            }
            throw new PscServiceException(
                    MessageFormat.format("Error Retrieving PSC details for {0}: {1} {2}", pscId,
                            e.getStatusCode(), e.getStatusMessage()), e);
        }
        catch (final URIValidationException | IOException e) {
            logger.errorContext(transaction.getId(), UNEXPECTED_STATUS_CODE, e, logMap);
            throw new PscServiceException(
                    MessageFormat.format("Error Retrieving PSC details for {0}: {1}", pscId,
                            e.getMessage()), e);
        }
    }
}
