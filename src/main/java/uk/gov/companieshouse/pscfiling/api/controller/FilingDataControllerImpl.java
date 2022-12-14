package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@RestController
@RequestMapping("/private/transactions/{transId}/persons-with-significant-control/{pscType}")
public class FilingDataControllerImpl implements FilingDataController {
    private final FilingDataService filingDataService;
    private final TransactionService transactionService;
    private final Logger logger;

    public FilingDataControllerImpl(final FilingDataService filingDataService,
                                    TransactionService transactionService, final Logger logger) {
        this.filingDataService = filingDataService;
        this.transactionService = transactionService;
        this.logger = logger;
    }

    /**
     * Controller endpoint: retrieve Filing Data. Returns a list containing a single resource;
     * Future capability to return multiple resources if a Transaction contains multiple PSC
     * Filings.
     *
     * @param transId        the Transaction ID
     * @param filingResource the Filing Resource ID
     * @param request        the servlet request
     * @return List of FilingApi resources
     */
    @Override
    @GetMapping(value = "/{filingResourceId}/filings", produces = {"application/json"})
    public List<FilingApi> getFilingsData(@PathVariable("transId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            final HttpServletRequest request) {
        final var logMap = LogHelper.createLogMap(transId, filingResource);

        logger.debugRequest(request,
                "GET /private/transactions/{transId}/persons-with-significant-control/{filingId}/filings", logMap);

        final var passthroughHeader =
            request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        final var transaction = transactionService.getTransaction(transId, passthroughHeader);

        final var filingApi = filingDataService.generatePscFiling(filingResource, transaction, passthroughHeader);

        logMap.put("psc filing:", filingApi);
        logger.infoContext(transId, "psc filing data", logMap);

        return List.of(filingApi);
    }
}
