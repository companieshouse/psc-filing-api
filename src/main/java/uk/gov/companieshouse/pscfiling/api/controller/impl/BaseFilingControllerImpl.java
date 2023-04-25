package uk.gov.companieshouse.pscfiling.api.controller.impl;

import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

/**
 * Base class responsible for retrieving/updating transaction resources and handling patch failures
 */
public class BaseFilingControllerImpl {
    public static final String VALIDATION_STATUS = "validation_status";
    protected final TransactionService transactionService;
    protected final PscFilingService pscFilingService;
    protected final PscMapper filingMapper;
    protected final Clock clock;
    protected final Logger logger;

    /**
     * Construct a BaseFilingControllerImpl
     *
     * @param transactionService    the {@link TransactionService} dependency
     * @param pscFilingService      the {@link PscFilingService} dependency
     * @param filingMapper          the {@link PscMapper} dependency
     * @param clock                 the {@link Clock} dependency
     * @param logger                the {@link Logger} dependency
     */
    public BaseFilingControllerImpl(final TransactionService transactionService,
                                    final PscFilingService pscFilingService, final PscMapper filingMapper,
                                    final Clock clock, final Logger logger) {
        this.transactionService = transactionService;
        this.pscFilingService = pscFilingService;
        this.filingMapper = filingMapper;
        this.clock = clock;
        this.logger = logger;
    }

    protected static void checkBindingErrors(final BindingResult bindingResult) {
        final var validationErrors = Optional.ofNullable(bindingResult).map(Errors::getFieldErrors).map(ArrayList::new)
                .orElseGet(ArrayList::new);

        if (!validationErrors.isEmpty()) {
            throw new InvalidFilingException(validationErrors);
        }
    }

    protected String getPassthroughHeader(final HttpServletRequest request) {
        return request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
    }

    /**
     * Retrieves the transaction resource
     *
     * @param transId           the transaction ID.
     * @param transaction       the transaction resource.
     * @param logMap            a list of parameters to include in a log message
     * @param passthroughHeader the passthroughHeader, includes authorisation for transaction fetch
     */
    protected Transaction getTransaction(final String transId, Transaction transaction, final Map<String, Object> logMap,
                                         final String passthroughHeader) {
        if (transaction == null) {
            transaction = transactionService.getTransaction(transId, passthroughHeader);
        }

        logger.infoContext(transId, "transaction found", logMap);
        return transaction;
    }

    /**
     * Updates the transaction resource
     *
     * @param transaction       the transaction resource.
     * @param links             the links, providing resource uri and validation status links
     *
     */
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

}
