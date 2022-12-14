package uk.gov.companieshouse.pscfiling.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import javax.servlet.http.HttpServletRequest;

public interface ValidationStatusController {

    @GetMapping(value = "/{filingResourceId}/validation_status", produces = {"application/json"})
    default ValidationStatusResponse validate(@PathVariable("transId") String transId,
                                              @PathVariable("filingResourceId") String filingResource,
                                              final HttpServletRequest request) {

        throw new NotImplementedException();
    }
}
