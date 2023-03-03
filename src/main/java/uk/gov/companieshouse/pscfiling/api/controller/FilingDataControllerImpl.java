package uk.gov.companieshouse.pscfiling.api.controller;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.BadInternalStateException;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@RestController
@RequestMapping(
        "/private/transactions/{transactionId}/persons-with-significant-control")
public class FilingDataControllerImpl implements FilingDataController {
    private static final Pattern SELF_URI_PSC_TYPE_PATTERN = Pattern.compile(
            "/persons-with-significant-control/"
                    + "(?<pscType>individual|corporate-entity|legal-person)/");
    final PscFilingService pscFilingService;
    private final FilingDataService filingDataService;
    private final TransactionService transactionService;
    private final Logger logger;

    public FilingDataControllerImpl(final PscFilingService pscFilingService,
            final TransactionService transactionService, final FilingDataService filingDataService,
            final Logger logger) {
        this.pscFilingService = pscFilingService;
        this.transactionService = transactionService;
        this.filingDataService = filingDataService;
        this.logger = logger;
    }

    /**
     * Controller endpoint: retrieve Filing Data. Returns a list containing a single resource;
     * Future capability to return multiple resources if a Transaction contains multiple PSC
     * Filings.
     *
     * @param transId       the transaction ID
     * @param filingResource the Filing Resource ID
     * @param transaction    the Transaction
     * @param request        the servlet request
     * @return List of FilingApi resources
     */
    @Override
    @GetMapping(value = "/{filingResourceId}/filings", produces = {"application/json"})
    public List<FilingApi> getFilingsData(@PathVariable("transactionId") final String transId,
            @PathVariable("filingResourceId") final String filingResource,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId, filingResource);

        logger.debugRequest(request,
                "GET /private/transactions/{transactionId}/persons-with-significant-control"
                        + "/{filingId}/filings", logMap);

        final var passthroughHeader =
                request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        if (transaction == null) {
            transaction = transactionService.getTransaction(transId, passthroughHeader);
        }

        final var pscFiling = pscFilingService.get(filingResource, transId);

        var temp = pscFiling.orElseThrow(() -> new FilingResourceNotFoundException(
                "Filing resource not found: " + filingResource));

        final var self = temp.getLinks().getSelf().getPath();

        final var matcher = SELF_URI_PSC_TYPE_PATTERN.matcher(self);

        if (matcher.find()) {
            final var type = matcher.group("pscType");
            final var pscType = PscTypeConstants.nameOf(type).orElseThrow(); // cannot be empty

            //calls the api-sdk
            final var filingApi =
                    filingDataService.generatePscFiling(filingResource, pscType, transaction,
                            passthroughHeader);

            logMap.put("psc filing:", filingApi);
            logger.infoContext(transId, "psc filing data", logMap);

            return List.of(filingApi);

        }
        else {
            throw new BadInternalStateException(
                    MessageFormat.format("PSC type not supported for PSC ID {0}", filingResource));
        }
    }
}