package uk.gov.companieshouse.pscfiling.api.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;

public interface ValidationStatusController {

    @GetMapping(value = "/{filingResourceId}/validation_status", produces = {"application/json"})
    default ValidationStatusResponse validate(@PathVariable("transactionId") final String transId,
                                              @PathVariable("filingResourceId") String filingResource,
                                              @RequestAttribute("transaction") Transaction transaction,
                                              final HttpServletRequest request) {

        throw new NotImplementedException();
    }
}
