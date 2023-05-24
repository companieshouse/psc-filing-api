package uk.gov.companieshouse.pscfiling.api.controller.impl;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
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
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.pscfiling.api.controller.PscIndividualFilingController;
import uk.gov.companieshouse.pscfiling.api.error.RetrievalFailureReason;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidPatchException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@RestController
@RequestMapping(
    "/transactions/{transactionId}/persons-with-significant-control/{pscType:"
        + "(?:individual)}")
public class PscIndividualFilingControllerImpl extends BaseFilingControllerImpl implements
    PscIndividualFilingController {
    private static final String PATCH_RESULT_MSG = "PATCH result";
    private static final String STATUS_MSG = "status";
    private static final String PATCH_FAILED = "patch failed";
    private static final String ERROR_MSG = "error";

    private final PscIndividualFilingService pscIndividualFilingService;

    public PscIndividualFilingControllerImpl(final TransactionService transactionService,
        final PscFilingService pscFilingService,
        final PscIndividualFilingService pscIndividualFilingService,
        final PscMapper filingMapper, final Clock clock, final Logger logger) {
        super(transactionService, pscFilingService, filingMapper, clock, logger);
        this.pscIndividualFilingService = pscIndividualFilingService;
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
    public ResponseEntity<PscIndividualFiling> createFiling(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            @RequestBody @Valid @NotNull final PscIndividualDto dto,
            final BindingResult bindingResult, final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "POST", logMap);

        checkBindingErrors(bindingResult);

        transaction = getTransaction(transId, transaction, logMap, getPassthroughHeader(request));

        final var entity = filingMapper.map(dto);
        final var savedEntity = saveFilingWithLinks(entity, transId, request, logMap);
        updateTransactionResources(transaction, savedEntity.getLinks());

        return ResponseEntity.created(savedEntity.getLinks().getSelf())
                .body(savedEntity);
    }

    /**
     * Update a PSC Individual Filing resource by applying a JSON merge-patch.
     *
     * @param transId        the transaction ID
     * @param pscType        the PSC type
     * @param filingResource the Filing resource ID (RFC 7396)
     * @param mergePatch     details of the merge-patch to apply
     * @param request        the servlet request
     * @return OK response containing the populated Filing resource
     */
    @SuppressWarnings("unchecked")
    // Patch Validator will add List<FieldError> object to PatchResult
    @Override
    @Transactional
    @PatchMapping(value = "/{filingResourceId}", produces = {"application/json"},
            consumes = "application/merge-patch+json")
    public ResponseEntity<PscIndividualFiling> updateFiling(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            @RequestBody final @NotNull Map<String, Object> mergePatch,
            final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);
        final var patchResult = pscFilingService.get(filingResource).filter(
            f1 -> pscFilingService.requestMatchesResourceSelf(request, f1)).map(
            f -> pscIndividualFilingService.patch(filingResource, mergePatch)).orElse(
            new PatchResult(RetrievalFailureReason.FILING_NOT_FOUND));

        if (patchResult.failedRetrieval()) {
            final var reason = (RetrievalFailureReason) patchResult.getRetrievalFailureReason();

            logMap.put(STATUS_MSG, PATCH_FAILED);
            logMap.put(ERROR_MSG, "retrieval failure: " + reason);
            logger.debugContext(transId, PATCH_RESULT_MSG, logMap);

            throw new FilingResourceNotFoundException(filingResource);
        }
        else if (patchResult.failedValidation()) {

            final var errors = (List<FieldError>) patchResult.getValidationErrors();

            logMap.put(STATUS_MSG, PATCH_FAILED);
            logMap.put(ERROR_MSG, "validation failure: " + errors);
            logger.debugContext(transId, PATCH_RESULT_MSG, logMap);

            throw new InvalidPatchException(errors);
        }
        else {
            logMap.put(STATUS_MSG, "patch successful");
            logger.debugContext(transId, PATCH_RESULT_MSG, logMap);

            Optional<PscCommunal> optionalFiling = pscFilingService.get(filingResource);

            return optionalFiling
                    .map(PscIndividualFiling.class::cast)
                    .map(PscIndividualFilingControllerImpl::createOKResponse)
                    .orElse(ResponseEntity.notFound()
                            .build());
        }

    }

    private static ResponseEntity<PscIndividualFiling> createOKResponse(PscIndividualFiling filing) {

        final var responseHeaders = new HttpHeaders();

        responseHeaders.setLocation(filing.getLinks().getSelf());

        return ResponseEntity.ok().headers(responseHeaders).body(filing);
    }

    /**
     * Retrieve PSC Filing submission for review by the user before completing the submission.
     *
     * @param transId        the Transaction ID
     * @param filingResource the PSC Filing ID
     * @return OK response containing Filing resource
     */
    @Override
    @GetMapping(value = "/{filingResourceId}", produces = {"application/json"})
    public ResponseEntity<PscIndividualFiling> getFilingForReview(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            final HttpServletRequest request) {

        final var maybePSCFiling = pscIndividualFilingService.get(filingResource)
                .filter(f -> pscFilingService.requestMatchesResourceSelf(request,
                        f));

        return maybePSCFiling.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    private PscIndividualFiling saveFilingWithLinks(final PscIndividualFiling entity, final String transId,
                                                    final HttpServletRequest request,
                                                    final Map<String, Object> logMap) {

        logger.debugContext(transId, "saving PSC filing", logMap);

        final var now = clock.instant();
        final var entityWithCreatedUpdated =
                PscIndividualFiling.builder(entity).createdAt(now).updatedAt(now)
                        .build();
        final var saved = pscIndividualFilingService.save(entityWithCreatedUpdated);
        final var links = buildLinks(request, saved);
        final var updatedWithLinks = PscIndividualFiling.builder(saved).links(links)
                .build();
        final var resaved = pscIndividualFilingService.save(updatedWithLinks);

        logMap.put("filing_id", resaved.getId());
        logger.infoContext(transId, "Filing saved", logMap);

        return resaved;
    }

    private Links buildLinks(final HttpServletRequest request, final PscIndividualFiling savedFiling) {
        final var objectId = new ObjectId(Objects.requireNonNull(savedFiling.getId()));
        final var selfUri = UriComponentsBuilder
                .fromUriString(request.getRequestURI())
                .pathSegment(objectId.toHexString())
                .build().toUri();

        final var validateUri = UriComponentsBuilder
                .fromUriString(request.getRequestURI()
                .replace(StringUtils.join("/", PscTypeConstants.INDIVIDUAL.getValue()), ""))
                .pathSegment(objectId.toHexString())
                .pathSegment(VALIDATION_STATUS)
                .build().toUri();

        return new Links(selfUri, validateUri);
    }
}
