package uk.gov.companieshouse.pscfiling.api.controller;

import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
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
    private String passThroughHeader;

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
        if (passThroughHeader == null) {
            passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        }
        return passThroughHeader;
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
        Objects.requireNonNull(passThroughHeader);
        final var resourceMap = buildResourceMap(links);

        transaction.setResources(resourceMap);
        transactionService.updateTransaction(transaction, passThroughHeader);
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
}