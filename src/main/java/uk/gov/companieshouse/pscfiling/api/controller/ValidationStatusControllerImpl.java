package uk.gov.companieshouse.pscfiling.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
    @RequestMapping("/private/transactions/{transId}/persons-with-significant-control/{pscType}")
    public class ValidationStatusControllerImpl implements ValidationStatusController {
        private final PscFilingService pscFilingService;
        private final Logger logger;


    public ValidationStatusControllerImpl(PscFilingService pscFilingService, Logger logger) {
            this.pscFilingService = pscFilingService;
            this.logger = logger;
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

            return maybePscIndividualFiling.map(this::isValid).orElseThrow(() -> new FilingResourceNotFoundException("Filing resource not found"));
        }

        private ValidationStatusResponse isValid(PscIndividualFiling pscFiling) {
            var validationStatus = new ValidationStatusResponse();
            validationStatus.setValid(true);
            return validationStatus;
        }
    }