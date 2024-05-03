package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

/**
 * Filing data controller that handles filing data.
 */
public interface FilingDataController {

    /**
     * Controller endpoint: retrieve Filing Data.
     *
     * @param transId       the transaction ID
     * @param pscType        the PSC type
     * @param filingResource the Filing Resource ID
     * @param transaction    the Transaction
     * @param request        the servlet request
     * @throws NotImplementedException implementing classes must perform work
     */
    @GetMapping
    default List<FilingApi> getFilingsData(@PathVariable("transactionId") final String transId,
                                           @PathVariable("pscType") PscTypeConstants pscType,
                                           @PathVariable("filingResource") String filingResource,
                                           @RequestAttribute("transaction") Transaction transaction,
                                           HttpServletRequest request){
        throw new NotImplementedException();
    }
}
