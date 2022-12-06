package uk.gov.companieshouse.pscfiling.api.service;

import java.io.IOException;
import java.text.MessageFormat;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.PSCServiceException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@Service
public class PscDetailsServiceImpl implements PscDetailsService {
    private static final String INVALID_STATUS_CODE = "Invalid Status Code received";

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
     * @throws PSCServiceException if PSC details not found or an error occurred
     */
    @Override
    public PscApi getPscDetails(final Transaction transaction, String pscId,
            PscTypeConstants pscType, final String ericPassThroughHeader)
            throws PSCServiceException {
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
            logger.errorContext(transaction.getId(), INVALID_STATUS_CODE, e, logMap);
            throw new PSCServiceException(
                    String.format("Error Retrieving PSC details for %s", pscId), e);
        }
        catch (final URIValidationException | IOException e) {
            logger.errorContext(transaction.getId(), INVALID_STATUS_CODE, e, logMap);
            throw new PSCServiceException(
                    MessageFormat.format("Error Retrieving PSC details for {0}: {1}", pscId,
                            e.getMessage()), e);
        }
    }
}
