package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public interface FilingDataController {

    /**
     * Controller endpoint: retrieve Filing Data.
     *
     * @param transId        the Transaction ID
     * @param filingResource the Filing Resource ID
     * @param request        the servlet request
     * @throws NotImplementedException implementing classes must perform work
     */
    @GetMapping
    default List<FilingApi> getFilingsData(@PathVariable("transId") String transId,
                                           @PathVariable("pscType") PscTypeConstants pscType,
                                           @PathVariable("filingResource") String filingResource, HttpServletRequest request){
        throw new NotImplementedException();
    }
}
