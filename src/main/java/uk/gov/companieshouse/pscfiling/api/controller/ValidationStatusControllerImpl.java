package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.ErrorMapper;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;
import uk.gov.companieshouse.pscfiling.api.validator.IndividualFilingValidationContext;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@RestController
@RequestMapping("/transactions/{transactionId}/persons-with-significant-control/")
public class ValidationStatusControllerImpl implements ValidationStatusController {
    public static final String TRANSACTION_NOT_SUPPORTED_ERROR =
            "Transaction not supported: FEATURE_FLAG_TRANSACTIONS_CLOSABLE=false";
    private static final Pattern SELF_URI_PSC_TYPE_PATTERN = Pattern.compile("/persons-with-significant-control/(?<pscType>individual|corporate-entity|legal-person)/");

    private final PscIndividualFilingService pscIndividualFilingService;
    private final TransactionService transactionService;
    private final FilingValidationService filingValidationService;
    private final PscIndividualMapper filingMapper;
    private final ErrorMapper errorMapper;
    private final Logger logger;
    private final boolean isTransactionsCloseableEnabled;

    public ValidationStatusControllerImpl(final PscIndividualFilingService pscIndividualFilingService,
            final TransactionService transactionService, final FilingValidationService filingValidationService,
            final PscIndividualMapper filingMapper, final ErrorMapper errorMapper, @Value("#{new Boolean('${feature.flag.transactions.closable}')}") final boolean isTransactionsClosableEnabled,
            Logger logger) {
        this.pscIndividualFilingService = pscIndividualFilingService;
        this.transactionService = transactionService;
        this.filingValidationService = filingValidationService;
        this.filingMapper = filingMapper;
        this.errorMapper = errorMapper;
        this.isTransactionsCloseableEnabled = isTransactionsClosableEnabled;
        this.logger = logger;

        logger.info(String.format("Setting \"feature.flag.transactions.closable\" to: %s", isTransactionsClosableEnabled));
    }

    @Override
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{filingResourceId}/validation_status", produces = {"application/json"})
    public ValidationStatusResponse validate(@PathVariable("transactionId") final String transId,
            @PathVariable("filingResourceId") final String filingResource,
            final HttpServletRequest request) {

        final var logMap = LogHelper.createLogMap(transId, filingResource);
        logMap.put("path", request.getRequestURI());
        logMap.put("method", request.getMethod());
        logger.debugRequest(request, "GET validation request", logMap);

        final var passthroughHeader =
                request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());

        final var maybePscIndividualFiling = pscIndividualFilingService.get(filingResource, transId);

        return maybePscIndividualFiling.map(pscFiling -> isValid(pscFiling, transId, passthroughHeader))
                .orElseThrow(() -> new FilingResourceNotFoundException(
                        "Filing resource not found: " + filingResource));
    }

    private ValidationStatusResponse isValid(final PscIndividualFiling pscFiling, final String transId,
            final String passthroughHeader) {

        final var validationStatus = new ValidationStatusResponse();

        if (isTransactionsCloseableEnabled) {
            final var validationErrors = calculateIsValid(pscFiling, transId, passthroughHeader);

            validationStatus.setValid(validationErrors.length == 0);
            validationStatus.setValidationStatusError(validationErrors);

        }
        else {
            validationStatus.setValid(false);
            validationStatus.setValidationStatusError(new ValidationStatusError[]{
                    new ValidationStatusError(TRANSACTION_NOT_SUPPORTED_ERROR, null, null,
                            ErrorType.SERVICE.getType())
            });
        }

        return validationStatus;
    }

    private ValidationStatusError[] calculateIsValid(final PscIndividualFiling pscFiling, final String transId,
            final String passthroughHeader) {
        final var self = pscFiling.getLinks().getSelf().getPath();
        final var matcher = SELF_URI_PSC_TYPE_PATTERN.matcher(self);

        if (matcher.find()) {
            final var type = matcher.group("pscType");
            final var pscType = PscTypeConstants.nameOf(type).orElseThrow(); // cannot be empty

            return validatePscType(pscFiling, transId, passthroughHeader, pscType);
        }
        else {
            return new ValidationStatusError[]{
                    new ValidationStatusError("PSC type could not be identified", "$.links.self",
                            LocationType.RESOURCE.getValue(), ErrorType.VALIDATION.getType())
            };

        }

    }

    private ValidationStatusError[] validatePscType(final PscIndividualFiling pscFiling, final String transId,
            final String passthroughHeader, final PscTypeConstants pscType) {
        final var dto = filingMapper.map(pscFiling);
        final var errors = new ArrayList<FieldError>();
        // TODO: When Transaction Interceptor is implemented, transaction will be in HTTP request
        final var transaction =
                transactionService.getTransaction(transId, passthroughHeader);
        final var context = new IndividualFilingValidationContext(dto, errors, transaction,
                pscType, passthroughHeader);

        filingValidationService.validate(context);

        return errorMapper.map(context.getErrors());
    }

}