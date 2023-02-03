package uk.gov.companieshouse.pscfiling.api.controller;

import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@RestController
@RequestMapping("/transactions/{transactionId}/persons-with-significant-control/individual")
public class PscIndividualFilingControllerImpl implements PscIndividualFilingController {
    public static final String VALIDATION_STATUS = "validation_status";
    private final TransactionService transactionService;
    private final PscIndividualFilingService pscIndividualFilingService;
    private final PscIndividualMapper filingMapper;
    private final FilingValidationService validatorService;
    private final Clock clock;
    private final Logger logger;

    public PscIndividualFilingControllerImpl(final TransactionService transactionService,
            final PscIndividualFilingService pscIndividualFilingService, final PscIndividualMapper filingMapper,
            final FilingValidationService validatorService, final Clock clock,
            final Logger logger) {
        this.transactionService = transactionService;
        this.pscIndividualFilingService = pscIndividualFilingService;
        this.filingMapper = filingMapper;
        this.validatorService = validatorService;
        this.clock = clock;
        this.logger = logger;
    }

    /**
     * Create an PSC Filing.
     *
     * @param transId       the Transaction ID
     * @param dto           the request body payload DTO
     * @param bindingResult the MVC binding result (with any validation errors)
     * @param request       the servlet request
     * @return CREATED response containing the populated Filing resource
     */
    @Override
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Object> createFiling(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestBody @Valid @NotNull final PscIndividualDto dto,
            final BindingResult bindingResult, final HttpServletRequest request) {
        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "POST", logMap);

        final var validationErrors = Optional.ofNullable(bindingResult)
                .map(Errors::getFieldErrors).map(ArrayList::new)
                .orElseGet(ArrayList::new);
        final var passthroughHeader =
                request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        final var transaction = transactionService.getTransaction(transId, passthroughHeader);
        logger.infoContext(transId, "transaction found", logMap);

        validatorService.validate(
                new FilingValidationContext(dto, validationErrors, transaction, pscType,
                        passthroughHeader));
        if (!validationErrors.isEmpty()) {
            throw new InvalidFilingException(validationErrors);
        }

        final var entity = filingMapper.map(dto);
        final var links = saveFilingWithLinks(entity, transId, request, logMap);
        final var resourceMap = buildResourceMap(links);

        transaction.setResources(resourceMap);
        transactionService.updateTransaction(transaction, passthroughHeader);

        return ResponseEntity.created(links.getSelf())
                .build();
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
    public ResponseEntity<PscIndividualDto> getFilingForReview(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource) {

        final var maybePSCFiling = pscIndividualFilingService.get(filingResource, transId);

        final var maybeDto = maybePSCFiling.map(filingMapper::map);

        return maybeDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
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

    private Links saveFilingWithLinks(final PscIndividualFiling entity, final String transId,
                                      final HttpServletRequest request, final Map<String, Object> logMap) {
        final var saved = pscIndividualFilingService.save(entity, transId);
        final var links = buildLinks(saved, request);
        final var updated = PscIndividualFiling.builder(saved).links(links)
                .build();
        final var resaved = pscIndividualFilingService.save(updated, transId);

        logMap.put("filing_id", resaved.getId());
        logger.infoContext(transId, "Filing saved", logMap);

        return links;
    }

    // TODO Refactor this to handle PSC types other than Individual, in both the arguments and the path build
    private Links buildLinks(final PscIndividualFiling savedFiling, final HttpServletRequest request) {
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
