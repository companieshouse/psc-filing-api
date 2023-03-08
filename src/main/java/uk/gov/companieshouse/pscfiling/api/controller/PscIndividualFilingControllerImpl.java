package uk.gov.companieshouse.pscfiling.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import java.time.Clock;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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
import uk.gov.companieshouse.pscfiling.api.exception.InvalidPatchException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.utils.MapHelper;

@RestController
@RequestMapping(
        "/transactions/{transactionId}/persons-with-significant-control/{pscType:"
                + "(?:individual)}")
public class PscIndividualFilingControllerImpl extends BaseFilingControllerImpl implements PscIndividualFilingController {

    public PscIndividualFilingControllerImpl(final TransactionService transactionService,
                                             final PscFilingService pscFilingService, final PscMapper filingMapper,
                                             final Clock clock, final Logger logger) {
        super(transactionService, pscFilingService, filingMapper, clock, logger);
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
    public ResponseEntity<Object> createFiling(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            @RequestBody @Valid @NotNull final PscIndividualDto dto,
            final BindingResult bindingResult, final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "POST", logMap);

        checkBindingErrors(bindingResult);

        transaction = getTransaction(transId, transaction, logMap, getPassthroughHeader(request));

        final var entity = filingMapper.map(dto);
        final var links = saveFilingWithLinks(entity, transId, request, logMap);
        updateTransactionResources(transaction, links);

        return ResponseEntity.created(links.getSelf())
                .build();
    }

    /**
     * Update a PSC Individual Filing resource by applying a JSON merge-patch.
     *
     * @param transId        the transaction ID
     * @param pscType        the PSC type
     * @param filingResource the Filing resource ID
     * @param mergePatch     details of the merge-patch to apply
     * @param bindingResult  the MVC binding result (with any validation errors)
     * @param request        the servlet request
     * @return CREATED response containing the populated Filing resource
     */
    @Override
    @Transactional
    @PatchMapping(value = "/{filingResourceId}", produces = {"application/json"},
            consumes = {"application/merge-patch+json"})
    public ResponseEntity<PscIndividualDto> updateFiling(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType, @PathVariable("filingResourceId") final String filingResource,
            @RequestBody @Valid @NotNull final JsonMergePatch mergePatch,
            final BindingResult bindingResult, final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "PATCH", logMap);

        final var validationErrors = Optional.ofNullable(bindingResult)
                .map(Errors::getFieldErrors).map(ArrayList::new)
                .orElseGet(ArrayList::new);

        if (!validationErrors.isEmpty()) {
            throw new InvalidPatchException(validationErrors);
        }

        final var maybePSCFiling = pscFilingService.get(filingResource, transId);

        // TODO set PscIndividualFiling.updatedAt

        final var pscIndividualFiling = maybePSCFiling.map(f -> mergePatchFiling(f, mergePatch));
        final var pscIndividualFiling1 =
                pscIndividualFiling.map(p -> pscFilingService.save(p, transId));
        final var pscIndividualDto = pscIndividualFiling1.map(filingMapper::map);
        final var pscIndividualDtoResponseEntity = pscIndividualDto.map(ResponseEntity::ok);
        return pscIndividualDtoResponseEntity
                .orElse(ResponseEntity.notFound()
                        .build());


    }

    private static PscIndividualFiling mergePatchFiling(final PscCommunal patchTarget,
            final JsonMergePatch mergePatch) {
        final var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
        try {
            final var patched =
                    mergePatch.apply(objectMapper.convertValue(patchTarget, JsonNode.class));

            return objectMapper.treeToValue(patched, PscIndividualFiling.class);
        }
        catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Transactional
    @PatchMapping(value = "/{filingResourceId}/alt", produces = {"application/json"},
            consumes = {"application/merge-patch+json"})
    public ResponseEntity<PscIndividualDto> updateFilingAlternate(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType, @PathVariable("filingResourceId") final String filingResource,
            @RequestBody @Valid @NotNull final PscIndividualDto dto,
            final BindingResult bindingResult, final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "PATCH", logMap);

        final var validationErrors = Optional.ofNullable(bindingResult)
                .map(Errors::getFieldErrors).map(ArrayList::new)
                .orElseGet(ArrayList::new);

        if (!validationErrors.isEmpty()) {
            throw new InvalidPatchException(validationErrors);
        }

        final var maybePSCFiling = pscFilingService.get(filingResource, transId);

        // TODO set PscIndividualFiling.updatedAt

        final var pscIndividualFiling = maybePSCFiling.map(f -> patchFilingAlternate((PscIndividualFiling) f, filingMapper.map(dto)));
        final var pscIndividualFiling1 =
                pscIndividualFiling.map(p -> pscFilingService.save(p, transId));
        final var pscIndividualDto = pscIndividualFiling1.map(filingMapper::map);
        final var pscIndividualDtoResponseEntity = pscIndividualDto.map(ResponseEntity::ok);
        return pscIndividualDtoResponseEntity
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    private PscIndividualFiling patchFilingAlternate(final PscIndividualFiling original,
            final PscIndividualFiling patch) {
        final Map<String, Object> fieldMap = new HashMap<>();
        PscIndividualFiling mergedFiling = null;

        extractFields(original, fieldMap);
        extractFields(patch, fieldMap);

        var mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        var updatedFiling = mapper.createObjectNode();

        for(final Map.Entry<String,Object> entry : fieldMap.entrySet()){
            var field = entry.getKey();
            var value = entry.getValue();
            updatedFiling.set(field, mapper.valueToTree(value)); // handle nested objects
        }

        var updatedFilingJson = updatedFiling.toString();

        try {
            mergedFiling = mapper.readerFor(PscIndividualFiling.class).readValue(updatedFilingJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return mergedFiling;
    }

    private void extractFields(PscIndividualFiling filing, Map<String,Object> fieldMap){
        final Map<String, Object> originalMap =
                MapHelper.convertObject(filing, PropertyNamingStrategies.LOWER_CAMEL_CASE);
        fieldMap.putAll(originalMap);
        // Remove some extra entries here to avoid extra string comparisons during the merge
        // These will be added to the record on load and cause issues when converting from JSON
        fieldMap.remove("class");
        fieldMap.remove("links");
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
    public ResponseEntity<PscDtoCommunal> getFilingForReview(
            @PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @PathVariable("filingResourceId") final String filingResource,
            final HttpServletRequest request) {

        final var maybePSCFiling = pscFilingService.get(filingResource, transId);

        final var maybeDto = maybePSCFiling.filter(f -> pscFilingService.requestMatchesResource(request, f)).map(filingMapper::map);

        return maybeDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    private Links saveFilingWithLinks(final PscIndividualFiling entity, final String transId,
                                      final HttpServletRequest request, final Map<String, Object> logMap) {
        final var saved = pscFilingService.save(entity, transId);
        final var links = buildLinks(request, saved);
        final var updated = PscIndividualFiling.builder(saved).links(links)
                .build();
        final var resaved = pscFilingService.save(updated, transId);

        logMap.put("filing_id", resaved.getId());
        logger.infoContext(transId, "Filing saved", logMap);

        return links;
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
