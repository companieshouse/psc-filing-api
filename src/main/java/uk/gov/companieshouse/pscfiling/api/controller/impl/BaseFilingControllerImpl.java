package uk.gov.companieshouse.pscfiling.api.controller.impl;

import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.pscfiling.api.error.RetrievalFailureReason;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidPatchException;
import uk.gov.companieshouse.pscfiling.api.exception.PscFilingServiceException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

public class BaseFilingControllerImpl {
    public static final String VALIDATION_STATUS = "validation_status";
    protected final TransactionService transactionService;
    protected final PscFilingService pscFilingService;
    protected final PscMapper filingMapper;
    protected final Clock clock;
    protected final Logger logger;

    public BaseFilingControllerImpl(final TransactionService transactionService,
                                    final PscFilingService pscFilingService, final PscMapper filingMapper,
                                    final Clock clock, final Logger logger) {
        this.transactionService = transactionService;
        this.pscFilingService = pscFilingService;
        this.filingMapper = filingMapper;
        this.clock = clock;
        this.logger = logger;
    }

    protected static void checkBindingErrors(BindingResult bindingResult) {
        final var validationErrors = Optional.ofNullable(bindingResult).map(Errors::getFieldErrors).map(ArrayList::new)
                .orElseGet(ArrayList::new);

        if (!validationErrors.isEmpty()) {
            throw new InvalidFilingException(validationErrors);
        }
    }

    protected String getPassthroughHeader(HttpServletRequest request) {
        return request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
    }

    protected Transaction getTransaction(String transId, Transaction transaction, Map<String, Object> logMap,
                                         String passthroughHeader) {
        if (transaction == null) {
            transaction = transactionService.getTransaction(transId, passthroughHeader);
        }

        logger.infoContext(transId, "transaction found", logMap);
        return transaction;
    }

    protected void updateTransactionResources(Transaction transaction, Links links) {
        final var resourceMap = buildResourceMap(links);
        transaction.setResources(resourceMap);
        transactionService.updateTransaction(transaction);
    }

    private Map<String, Resource> buildResourceMap(final Links links) {
        final Map<String, Resource> resourceMap = new HashMap<>();
        final var resource = new Resource();
        final var linksMap = new HashMap<>(
                Map.of("resource", links.getSelf().toString(), VALIDATION_STATUS,
                        links.getValidationStatus().toString()));

        resource.setKind("psc-filing");
        resource.setLinks(linksMap);
        resource.setUpdatedAt(clock.instant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        resourceMap.put(links.getSelf().toString(), resource);
        return resourceMap;
    }

    protected RuntimeException handlePatchFailed(final String transId, final String filingResource,
            final HttpServletRequest request, final Map<String, Object> logMap,
            final PatchResult patchResult) {
        final RuntimeException exception;

        if (patchResult.failedValidation()) {
            exception = new InvalidPatchException(
                    List.of((FieldError) patchResult.getValidationErrors()));

        }
        else if (patchResult.failedRetrieval()) {
            final var reason = (RetrievalFailureReason) patchResult.getRetrievalFailureReason();

            logMap.put("error", "retrieval failure: " + reason);
            logger.debugRequest(request, "PATCH", logMap);

            exception = new FilingResourceNotFoundException("Failed to retrieve filing: " + filingResource);
        }
        else {
            logMap.put("status", "patch invalid");
            logger.errorContext(transId, "patch failed", null, logMap);
            exception = new PscFilingServiceException("Failed to retrieve filing: " + filingResource, null);
        }

        return exception;
    }
}
