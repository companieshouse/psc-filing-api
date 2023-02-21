package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public interface FilingDataController {

    /**
     * Controller endpoint: retrieve Filing Data.
     *
     * @param pscType        the PSC type
     * @param filingResource the Filing Resource ID
     * @param transaction    the Transaction
     * @param request        the servlet request
     * @throws NotImplementedException implementing classes must perform work
     */
    @GetMapping
    default List<FilingApi> getFilingsData(@PathVariable("pscType") PscTypeConstants pscType,
                                           @PathVariable("filingResource") String filingResource,
                                           @RequestAttribute("transaction") Transaction transaction,
                                           HttpServletRequest request){
        throw new NotImplementedException();
    }
}
