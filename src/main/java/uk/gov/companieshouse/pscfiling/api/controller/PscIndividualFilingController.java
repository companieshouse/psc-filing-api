package uk.gov.companieshouse.pscfiling.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

public interface PscIndividualFilingController {
    /**
     * Create an PSC Filing for an Individual.
     * @param transId       the transaction ID
     * @param pscType       the PSC type
     * @param transaction   the Transaction
     * @param dto           the request body payload DTO
     * @param result        the MVC binding result (with any validation errors)
     * @param request       the servlet request
     * @throws NotImplementedException implementing classes must perform work
     */
    @PostMapping
    default ResponseEntity<Object> createFiling(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestAttribute("transaction") Transaction transaction,
            @RequestBody @Valid @NotNull final PscIndividualDto dto,
            final BindingResult result,
            final HttpServletRequest request) {
        throw new NotImplementedException();
    }

    /**
     * Retrieve PSC Filing submission for review by the user before completing the submission.
     *
     * @param transId        the Transaction ID
     * @param filingResource the PSC Filing ID
     * @throws NotImplementedException implementing classes must perform work
     */
    @GetMapping
    default ResponseEntity<PscDtoCommunal> getFilingForReview(
            @PathVariable("transactionId") String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResource") String filingResource,
            final HttpServletRequest request) {
        throw new NotImplementedException();
    }
}
