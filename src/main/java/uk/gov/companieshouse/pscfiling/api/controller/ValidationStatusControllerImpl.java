package uk.gov.companieshouse.pscfiling.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/transactions/{transId}/persons-with-significant-control/")
public class ValidationStatusControllerImpl implements ValidationStatusController {
    public static final String TRANSACTION_NOT_SUPPORTED_ERROR =
            "Transaction not supported: FEATURE_FLAG_TRANSACTIONS_CLOSABLE=false";
    private final PscFilingService pscFilingService;
    private final Logger logger;
    private final boolean isTransactionsCloseableEnabled;

    public ValidationStatusControllerImpl(PscFilingService pscFilingService,
            @Value("#{new Boolean('${feature.flag.transactions.closable}')}") final boolean isTransactionsClosableEnabled, Logger logger) {
        this.pscFilingService = pscFilingService;
        this.isTransactionsCloseableEnabled = isTransactionsClosableEnabled;
        this.logger = logger;

        logger.info(String.format("Setting \"feature.flag.transactions.closable\" to: %s", isTransactionsClosableEnabled));
    }

    @Override
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{filingResourceId}/validation_status", produces = {"application/json"})
    public ValidationStatusResponse validate(@PathVariable("transId") final String transId,
            @PathVariable("filingResourceId") final String filingResource,
            final HttpServletRequest request) {

        final Map<String, Object> logMap;
        logMap = LogHelper.createLogMap(transId, filingResource);
        logMap.put("path", request.getRequestURI());
        logMap.put("method", request.getMethod());
        logger.debugRequest(request, "GET validation request", logMap);

        var maybePscIndividualFiling = pscFilingService.get(filingResource, transId);

        return maybePscIndividualFiling.map(this::isValid)
                .orElseThrow(() -> new FilingResourceNotFoundException(
                        "Filing resource not found: " + filingResource));
    }

    private ValidationStatusResponse isValid(PscIndividualFiling pscFiling) {

        var validationStatus = new ValidationStatusResponse();

        if(isTransactionsCloseableEnabled){
            validationStatus.setValid(calculateIsValid());

        } else {
            validationStatus.setValidationStatusError(new ValidationStatusError[]{
                    new ValidationStatusError(TRANSACTION_NOT_SUPPORTED_ERROR, null, null, ErrorType.SERVICE.getType())
            });
        }

        return validationStatus;
    }

    //TODO - proper validation needs to be implemented - as this is a temporary solution
    private boolean calculateIsValid() {
        return true;
    }

}