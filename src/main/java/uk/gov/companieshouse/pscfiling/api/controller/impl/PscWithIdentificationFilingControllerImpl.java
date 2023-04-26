package uk.gov.companieshouse.pscfiling.api.controller.impl;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.controller.PscWithIdentificationFilingController;
import uk.gov.companieshouse.pscfiling.api.error.RetrievalFailureReason;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidPatchException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.PscWithIdentificationFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

/**
 * PSC with identification filing controller that handles legal person and corporate entity data.
 */
@RestController
@RequestMapping("/transactions/{transactionId}/persons-with-significant-control/{pscType:"
        + "(?:legal-person|corporate-entity)}")
public class PscWithIdentificationFilingControllerImpl extends BaseFilingControllerImpl
        implements PscWithIdentificationFilingController {

    private static final String PATCH_RESULT_MSG = "PATCH result";
    private static final String STATUS_MSG = "status";

    private final PscWithIdentificationFilingService pscWithIdentificationFilingService;

    public PscWithIdentificationFilingControllerImpl(final TransactionService transactionService,
            final PscFilingService pscFilingService,
            final PscWithIdentificationFilingService pscWithIdentificationFilingService,
            final PscMapper filingMapper,
            final Clock clock, final Logger logger) {
        super(transactionService, pscFilingService, filingMapper, clock, logger);

        this.pscWithIdentificationFilingService = pscWithIdentificationFilingService;
    }

    /**
     * Create an PSC Filing.
     *
     * @param transId       the transaction ID
     * @param pscType       the PSC type
     * @param transaction   the Transaction
     * @param dto           the request body payload DTO
     * @param bindingResult the MVC binding result (with any validation errors)
     * @param request       the servlet request
     * @return CREATED response containing the populated Filing resource
     */
    @Override
    @Transactional
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PscWithIdentificationFiling> createFiling(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            @RequestBody @Valid @NotNull final PscWithIdentificationDto dto,
            final BindingResult bindingResult, final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "POST", logMap);

        checkBindingErrors(bindingResult);

        transaction = getTransaction(transId, transaction, logMap, getPassthroughHeader(request));

        final var entity = filingMapper.map(dto);
        final var savedEntity = saveFilingWithLinks(entity, transId, request, logMap, pscType);
        updateTransactionResources(transaction, savedEntity.getLinks());

        return ResponseEntity.created(savedEntity.getLinks().getSelf()).body(savedEntity);
    }

    /**
     * Update a PSC Individual Filing resource by applying a JSON merge-patch.
     *
     * @param transId        the transaction ID
     * @param pscType        the PSC type
     * @param filingResource the Filing resource ID (RFC 7396)
     * @param mergePatch     details of the merge-patch to apply
     * @param request        the servlet request
     * @return CREATED response containing the populated Filing resource
     */
    @SuppressWarnings("unchecked")
    // Patch Validator will add List<FieldError> object to PatchResult
    @Override
    @Transactional
    @PatchMapping(value = "/{filingResourceId}", produces = {"application/json"},
            consumes = "application/merge-patch+json")
    public ResponseEntity<PscWithIdentificationFiling> updateFiling(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            @RequestBody final @NotNull Map<String, Object> mergePatch,
            final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);
        final var patchResult = pscWithIdentificationFilingService.patch(filingResource,
                mergePatch);

        if (patchResult.failedRetrieval()) {
            final var reason = (RetrievalFailureReason) patchResult.getRetrievalFailureReason();

            logMap.put(STATUS_MSG, "patch failed");
            logMap.put("error", "retrieval failure: " + reason);
            logger.infoContext(transId, PATCH_RESULT_MSG, logMap);

            throw new FilingResourceNotFoundException(
                    "Failed to retrieve filing: " + filingResource);
        }
        else if (patchResult.failedValidation()) {
            final var errors = (List<FieldError>) patchResult.getValidationErrors();

            logMap.put(STATUS_MSG, "patch failed");
            logMap.put("error", "validation failure: " + errors);
            logger.infoContext(transId, PATCH_RESULT_MSG, logMap);

            throw new InvalidPatchException(errors);
        }
        else {
            logMap.put(STATUS_MSG, "patch successful");
            logger.infoContext(transId, PATCH_RESULT_MSG, logMap);

            return pscWithIdentificationFilingService.get(filingResource)
                    .map(PscWithIdentificationFiling.class::cast)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound()
                            .build());
        }

    }

    /**
     * Retrieve PSC Filing submission for review by the user before completing the submission.
     *
     * @param transId        the Transaction ID
     * @param filingResource the PSC Filing ID
     * @return OK response containing Filing DTO resource
     */
    @Override
    @GetMapping(value = "/{filingResourceId}", produces = {"application/json"})
    public ResponseEntity<PscWithIdentificationDto> getFilingForReview(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId, filingResource);
        logMap.put("path", request.getRequestURI());
        logMap.put("method", request.getMethod());
        logger.debugRequest(request, "GET filing resource", logMap);

        final var maybePSCFiling = pscWithIdentificationFilingService.get(filingResource);
        final var maybeDto =
                maybePSCFiling.filter(f -> pscFilingService.requestMatchesResourceSelf(request,
                        f)).map(filingMapper::map);

        return maybeDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    /**
     * Stores a filing in the database then updates with Links object.
     *
     * @param entity            the PSC filing
     * @param transId           the transaction ID
     * @param request           the HttpServletRequest
     * @param logMap            a list of parameters to include in a log message
     * @param pscType           the PSC type
     * @return PscWithIdentificationFiling
     */
    private PscWithIdentificationFiling saveFilingWithLinks(final PscWithIdentificationFiling entity,
                                                            final String transId, final HttpServletRequest request,
                                                            final Map<String, Object> logMap,
                                                            final PscTypeConstants pscType) {
        logger.debugContext(transId, "saving PSC filing", logMap);

        final var now = clock.instant();
        final var entityWithCreatedUpdated = PscWithIdentificationFiling.builder(entity)
                .createdAt(now)
                .updatedAt(now)
                .build();
        final var saved = pscWithIdentificationFilingService.save(entityWithCreatedUpdated);
        final var links = buildLinks(request, saved.getId(), pscType);
        final var updatedWithLinks = PscWithIdentificationFiling.builder(saved).links(links)
                .build();
        final var resaved = pscWithIdentificationFilingService.save(updatedWithLinks);

        logMap.put("filing_id", resaved.getId());
        logger.infoContext(transId, "Filing saved", logMap);

        return resaved;
    }

    /**
     * Builds a Links object containing a self uri link and a validation status link.
     *
     * @param request           the HttpServletRequest
     * @param savedFilingId     the PSC Filing ID
     * @param pscType           the PSC type
     * @return Links object
     */
    private Links buildLinks(final HttpServletRequest request, final String savedFilingId,
                             final PscTypeConstants pscType) {
        final var objectId = new ObjectId(Objects.requireNonNull(savedFilingId));
        final var selfUri = UriComponentsBuilder.fromUriString(request.getRequestURI())
                .pathSegment(objectId.toHexString())
                .build()
                .toUri();

        final var validateUri = UriComponentsBuilder.fromUriString(request.getRequestURI()
                        .replace(StringUtils.join("/", pscType.getValue()), ""))
                .pathSegment(objectId.toHexString())
                .pathSegment(VALIDATION_STATUS)
                .build()
                .toUri();

        return new Links(selfUri, validateUri);
    }
}