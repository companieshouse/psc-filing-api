package uk.gov.companieshouse.pscfiling.api.controller;

import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@RestController
@RequestMapping("/transactions/{transactionId}/persons-with-significant-control/{pscType:"
        + "(?:legal-person|corporate-entity)}")
public class PscWithIdentificationFilingControllerImpl extends BaseFilingControllerImpl implements PscWithIdentificationFilingController {

    public PscWithIdentificationFilingControllerImpl(final TransactionService transactionService,
                                                     final PscFilingService pscFilingService, final PscMapper filingMapper,
                                                     final Clock clock,
                                                     final Logger logger) {
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
    @PostMapping(produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<PscCommunal> createFiling(@PathVariable("transactionId") final String transId,
            @PathVariable("pscType") final PscTypeConstants pscType,
            @RequestAttribute(required = false, name = "transaction") Transaction transaction,
            @RequestBody @Valid @NotNull final PscWithIdentificationDto dto,
            final BindingResult bindingResult, final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId);

        logger.debugRequest(request, "POST", logMap);

        checkBindingErrors(bindingResult);

        transaction = getTransaction(transId, transaction, logMap, getPassthroughHeader(request));

        final var entity = filingMapper.map(dto);
        final var links = saveFilingWithLinks(entity, transId, request, logMap, pscType);
        updateTransactionResources(transaction, links);

        var responseEntity = pscFilingService.get(entity.getId(), transId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", links.getSelf().getPath());
        return new ResponseEntity(responseEntity, headers, HttpStatus.CREATED);
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

    private Links saveFilingWithLinks(final PscWithIdentificationFiling entity, final String transId,
                                      final HttpServletRequest request, final Map<String, Object> logMap,
                                      PscTypeConstants pscType) {
        final var saved = pscFilingService.save(entity, transId);
        final var links = buildLinks(request, saved.getId(), pscType);
        final var updated = PscWithIdentificationFiling.builder(saved).links(links)
                .build();
        final var resaved = pscFilingService.save(updated, transId);

        logMap.put("filing_id", resaved.getId());
        logger.infoContext(transId, "Filing saved", logMap);

        return links;
    }

    private Links buildLinks(final HttpServletRequest request, String savedFilingId,
                             PscTypeConstants pscType) {
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