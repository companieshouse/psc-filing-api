package uk.gov.companieshouse.pscfiling.api.error;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.CompanyProfileServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.PscFilingServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.TransactionServiceException;

/**
 * Handle exceptions caused by client REST requests, propagated from Spring or the service
 * controllers.
 * <ul>
 *     <li>JSON payload not readable/malformed</li>
 *     <li>{@link InvalidFilingException}</li>
 *     <li>{@link FilingResourceNotFoundException}</li>
 *     <li>{@link TransactionServiceException}</li>
 *     <li>{@link PscServiceException}</li>
 *     <li>other {@link RuntimeException}</li>
 *     <li>other internal exceptions</li>
 * </ul>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String CAUSE = "cause";
    private static final Pattern PARSE_MESSAGE_PATTERN = Pattern.compile("(Text .*)$", Pattern.MULTILINE);

    @Autowired
    @Qualifier(value = "validation")
    protected Map<String, String> validation;
    private final Logger chLogger;

    public RestExceptionHandler(Map<String, String> validation, final Logger logger) {
        this.validation = validation;
        this.chLogger = logger;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        final var cause = ex.getCause();
        final var baseMessage = "JSON parse error: ";
        final ApiError error;
        var message = "";

        if (cause instanceof JsonProcessingException) {
            final var jpe = (JsonProcessingException) cause;
            final var location = jpe.getLocation();
            var jsonPath = "$";
            Object rejectedValue = null;

            if (cause instanceof MismatchedInputException) {
                final var fieldNameOpt = ((MismatchedInputException) cause).getPath()
                        .stream()
                        .findFirst()
                        .map(JsonMappingException.Reference::getFieldName);
                jsonPath += fieldNameOpt.map(f -> "." + f).orElse("");

                message = getParseErrorMessage(cause.getMessage());
                if (jpe instanceof InvalidFormatException) {
                    rejectedValue = ((InvalidFormatException) cause).getValue();
                }
            }
            else {
                message = redactErrorMessage(cause.getMessage());
            }
            error = buildRequestBodyError(baseMessage + message, jsonPath, rejectedValue);
            addLocationInfo(error, location);
        }
        else {
            message = redactErrorMessage(ex.getMostSpecificCause().getMessage());
            error = buildRequestBodyError(baseMessage + message, "$", null);

        }
        logError(request, String.format("Message not readable: %s", message), ex);
        return ResponseEntity.badRequest().body(new ApiErrors(List.of(error)));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        logError(request, String.format("Media type not supported: %s", ex.getContentType()), ex);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .header(HttpHeaders.ACCEPT_PATCH, "application/merge-patch+json")
                .header("Accept-Post", "application/json")
                .build();
    }

    @ExceptionHandler(InvalidFilingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiErrors handleInvalidFilingException(final InvalidFilingException ex,
            WebRequest request) {
        final var fieldErrors = ex.getFieldErrors();

        final var errorList = fieldErrors.stream()
                .map(e -> buildRequestBodyError(e.getDefaultMessage(), getJsonPath(e),
                        e.getRejectedValue()))
                .collect(Collectors.toList());

        logError(request, "Invalid filing data", ex, errorList);
        return new ApiErrors(errorList);
    }

    @ExceptionHandler(ConflictingFilingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiErrors handleConflictingFilingException(final ConflictingFilingException ex,
                                                  WebRequest request) {
        final var fieldErrors = ex.getFieldErrors();

        final var errorList = fieldErrors.stream()
                .map(e -> buildRequestBodyError(e.getDefaultMessage(), getJsonPath(e),
                        e.getRejectedValue()))
                .collect(Collectors.toList());

        logError(request, "Conflicting filing data", ex, errorList);
        return new ApiErrors(errorList);
    }

    @ExceptionHandler(FilingResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiErrors handleResourceNotFoundException(final FilingResourceNotFoundException ex,
                                                     final WebRequest request) {
        final var error = new ApiError(validation.get("filing-resource-not-found"), getRequestURI(request),
                LocationType.RESOURCE.getValue(), ErrorType.VALIDATION.getType());

        Optional.ofNullable(ex.getMessage()).ifPresent(m -> error.addErrorValue("{filing-resource-id}", m));

        final var errorList = List.of(error);
        logError(request, ex.getMessage(), ex, errorList);
        return new ApiErrors(errorList);
    }

    @ExceptionHandler({
            PscServiceException.class,
            TransactionServiceException.class,
            CompanyProfileServiceException.class,
            PscFilingServiceException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiErrors handleServiceException(final Exception ex,
            final WebRequest request) {
        final var errorList = List.of(createApiServiceError(ex, request));
        logError(request, ex.getMessage(), ex, errorList);
        return new ApiErrors(errorList);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        chLogger.error("INTERNAL ERROR", ex);

        final var errorList = List.of(createApiServiceError(ex, request));
        logError(request, "Internal error", ex, errorList);
        return super.handleExceptionInternal(ex, new ApiErrors(errorList), headers, status,
                request);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiErrors handleAllUncaughtException(final RuntimeException ex,
            final WebRequest request) {
        final var errorList = List.of(createApiServiceError(ex, request));
        logError(request, "Unknown error", ex, errorList);
        return new ApiErrors(errorList);
    }

    private static ApiError createApiServiceError(final Exception ex, final WebRequest request) {
        final var error = new ApiError(ex.getMessage(), getRequestURI(request),
                LocationType.RESOURCE.getValue(), ErrorType.SERVICE.getType());
        Optional.ofNullable(ex.getCause())
                .ifPresent(c -> error.addErrorValue(CAUSE, c.getMessage()));

        return error;
    }

    private static void addLocationInfo(final ApiError error, final JsonLocation location) {
        error.addErrorValue("offset", location.offsetDescription());
        error.addErrorValue("line", String.valueOf(location.getLineNr()));
        error.addErrorValue("column", String.valueOf(location.getColumnNr()));
    }

    private static String getJsonPath(final FieldError e) {
        return Optional.ofNullable(e.getCodes())
                .stream()
                .flatMap(Arrays::stream)
                .skip(1)
                .findFirst()
                .map(s -> s.replaceAll("^[^.]*", "\\$"))
                .map(s -> s.replaceAll("([A-Z0-9]+)", "_$1").toLowerCase())
                .orElse(null);
    }

    private static String getParseErrorMessage(final String s) {
        final var matcher = PARSE_MESSAGE_PATTERN.matcher(s);

        return matcher.find() ? matcher.group(1) : "";
    }

    private String redactErrorMessage(final String s) {
        return StringUtils.substringBefore(s, ":");
    }

    private static String getRequestURI(final WebRequest request) {
        // resolveReference("request") preferred over getRequest() because the latter method is
        // final and cannot be stubbed with Mockito
        return Optional.ofNullable((HttpServletRequest) request.resolveReference("request"))
                .map(HttpServletRequest::getRequestURI)
                .orElse(null);
    }

    private static ApiError buildRequestBodyError(final String message, final String jsonPath,
            final Object rejectedValue) {
        final var error = new ApiError(message, jsonPath, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());

        Optional.ofNullable(rejectedValue)
                .map(Object::toString)
                .filter(Predicate.not(String::isEmpty))
                .ifPresent(r -> error.addErrorValue("rejected", r));

        return error;
    }

    private void logError(WebRequest request, String msg, Exception ex) {
        logError(request, msg, ex, null);
    }

    private void logError(WebRequest request, String msg, Exception ex,
            @Nullable List<ApiError> apiErrorList) {
        final Map<String, Object> logMap = new HashMap<>();
        final var servletRequest = ((ServletWebRequest) request).getRequest();
        logMap.put("path", servletRequest.getRequestURI());
        logMap.put("method", servletRequest.getMethod());
        Optional.ofNullable(apiErrorList).ifPresent(l -> logMap.put("errors", l));
        chLogger.error(msg, ex, logMap);
    }

}
