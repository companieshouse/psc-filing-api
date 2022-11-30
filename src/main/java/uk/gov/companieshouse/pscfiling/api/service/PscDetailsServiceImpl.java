package uk.gov.companieshouse.pscfiling.api.service;

import java.io.IOException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.PSCServiceException;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@Service
public class PscDetailsServiceImpl implements PscDetailsService {

    private final ApiClientService apiClientService;
    private final Logger logger;

    public PscDetailsServiceImpl(final ApiClientService apiClientService, Logger logger) {
        this.apiClientService = apiClientService;
        this.logger = logger;
    }

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
    @Override
    public PscApi getPscDetails(final Transaction transaction, String pscId, String pscType,
                                final String ericPassThroughHeader) throws PSCServiceException {
        try {
            final var uri = "/company/" + transaction.getCompanyNumber() + "/persons-with-significant-control/" + pscType + "/" + pscId;
            final var pscApi =
                    apiClientService.getOauthAuthenticatedClient(ericPassThroughHeader)
                            .pscs()
                            .getIndividual(uri)
                            .execute()
                            .getData();
            final var logMap = LogHelper.createLogMap(transaction.getId());
            logMap.put("company_number", transaction.getCompanyNumber());
            logMap.put("PSC name", pscApi.getName());
            logger.debugContext(transaction.getId(), "Retrieved PSC details", logMap);
            return pscApi;
        }
        catch (final URIValidationException | IOException e) {
            throw new PSCServiceException("Error Retrieving Psc details " + pscId,
                    e);
        }
    }
}
