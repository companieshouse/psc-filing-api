package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

public interface PscWithIdentificationFilingController {

    /**
     * Create an PSC Filing for Corporate (RLE) or Legal Entity.
     *
     * @param transId       the transaction ID
     * @param pscType       the PSC type
     * @param transaction   the Transaction
     * @param dto           the request body payload DTO
     * @param result        the MVC binding result (with any validation errors)
     * @param request       the servlet request
     * @throws NotImplementedException implementing classes must perform work
     */
    @PostMapping
    default ResponseEntity<PscWithIdentificationFiling> createFiling(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestAttribute("transaction") Transaction transaction,
            @RequestBody @Valid @NotNull final PscWithIdentificationDto dto,
            final BindingResult result,
            final HttpServletRequest request) {
        throw new NotImplementedException();
    }

    /**
     * Update a PSC Filing resource by applying a JSON merge-patch.
     *
     * @param transId        the transaction ID
     * @param pscType        the PSC type
     * @param filingResource the PSC Filing ID
     * @param mergePatch     details of the merge-patch to apply (RFC 7396)
     * @param request        the servlet request
     * @throws NotImplementedException implementing classes must perform work
     * @see <a href="https://www.rfc-editor.org/rfc/rfc7396">RFC7396</a>
     */
    @PatchMapping
    default ResponseEntity<PscWithIdentificationFiling> updateFiling(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType, @PathVariable("filingResource") String filingResource,
            @RequestBody final @NotNull Map<String, Object> mergePatch, final HttpServletRequest request) {
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
    default ResponseEntity<PscWithIdentificationDto> getFilingForReview(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResource") final String filingResource,
            final HttpServletRequest request) {
        throw new NotImplementedException();
    }

}