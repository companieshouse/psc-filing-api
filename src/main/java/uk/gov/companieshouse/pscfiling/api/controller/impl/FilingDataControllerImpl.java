package uk.gov.companieshouse.pscfiling.api.controller.impl;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.controller.FilingDataController;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@RestController
@RequestMapping(
        "/private/transactions/{transactionId}/persons-with-significant-control/{pscType:"
                + "(?:individual|corporate-entity|legal-person)}")
public class FilingDataControllerImpl implements FilingDataController {
    private final FilingDataService filingDataService;
    private final Logger logger;

    public FilingDataControllerImpl(final FilingDataService filingDataService, final Logger logger) {
        this.filingDataService = filingDataService;
        this.logger = logger;
    }

    /**
     * Controller endpoint: retrieve Filing Data. Returns a list containing a single resource;
     * Future capability to return multiple resources if a Transaction contains multiple PSC
     * Filings.
     *
     * @param transId       the transaction ID
     * @param pscType        the PSC type
     * @param filingResource the Filing Resource ID
     * @param transaction    the Transaction
     * @param request        the servlet request
     * @return List of FilingApi resources
     */
    @Override
    @GetMapping(value = "/{filingResourceId}/filings", produces = {"application/json"})
    public List<FilingApi> getFilingsData(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId, filingResource);

        logger.debugRequest(request,
                "GET /private/transactions/{transactionId}/persons-with-significant-control"
                        + "/{filingId}/filings", logMap);

        final var passthroughHeader =
                request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        final var filingApi =
                filingDataService.generatePscFiling(filingResource, pscType, transaction,
                        passthroughHeader);

        logMap.put("psc filing:", filingApi);
        logger.infoContext(transId, "psc filing data", logMap);

        return List.of(filingApi);
    }

}