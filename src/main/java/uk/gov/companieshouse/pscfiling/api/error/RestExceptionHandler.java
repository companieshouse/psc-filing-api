package uk.gov.companieshouse.pscfiling.api.error;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.CompanyProfileServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.MergePatchException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.TransactionServiceException;

/**
 * Handle exceptions caused by client REST requests, propagated from Spring or the service
 * controllers.
 * <ul>
 *     <li>JSON payload not readable/malformed</li>
 *     <li>{@link InvalidFilingException}</li>
 *     <li>{@link FilingResourceNotFoundException}</li>
 *     <li>{@link MergePatchException}</li>
 *     <li>{@link TransactionServiceException}</li>
 *     <li>{@link PscServiceException}</li>
 *     <li>other {@link RuntimeException}</li>
 *     <li>other internal exceptions</li>
 * </ul>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Pattern PARSE_MESSAGE_PATTERN = Pattern.compile("(Text .*)$",
            Pattern.MULTILINE);

    @Autowired
    @Qualifier(value = "validation")
    protected Map<String, String> validation;
    private final Logger chLogger;

    public RestExceptionHandler(final Map<String, String> validation, final Logger logger) {
        this.validation = validation;
        this.chLogger = logger;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull final HttpMessageNotReadableException ex,
                                                                  @NonNull final HttpHeaders headers,
                                                                  @NonNull final HttpStatusCode status,
                                                                  @NonNull final WebRequest request) {
        return createRedactedErrorResponseEntity(ex, request, ex.getCause(), validation.get("json-syntax-prefix"));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull final HttpMediaTypeNotSupportedException ex,
                                                                     @NonNull final HttpHeaders headers,
                                                                     @NonNull final HttpStatusCode status,
                                                                     @NonNull final WebRequest request) {
        logError(chLogger, request,
            String.format("Media type not supported: %s", ex.getContentType()), ex);

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .header(HttpHeaders.ACCEPT_PATCH, "application/merge-patch+json")
            .header("Accept-Post", "application/json")
            .build();
    }

    @ExceptionHandler(InvalidFilingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiErrors handleInvalidFilingException(final InvalidFilingException ex,
            final WebRequest request) {
        final var fieldErrors = ex.getFieldErrors();

        final List<ApiError> errorList = fieldErrors.stream()
                .map(e -> buildRequestBodyError(getFieldErrorApiEnumerationMessage(e),
                        getJsonPath(e), e.getRejectedValue()))
                .toList();

        logError(chLogger, request, "Invalid filing data", ex, errorList);
        return new ApiErrors(errorList);
    }

    @ExceptionHandler(MergePatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleMergePatchException(final MergePatchException ex,
            final WebRequest request) {
        return createRedactedErrorResponseEntity(ex, request, ex.getCause(),
                validation.get("patch-merge-error-prefix"));
    }

    @ExceptionHandler(ConflictingFilingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiErrors handleConflictingFilingException(final ConflictingFilingException ex,
                                                  final WebRequest request) {
        final var fieldErrors = ex.getFieldErrors();

        final var errorList = fieldErrors.stream()
                .map(e -> buildRequestBodyError(e.getDefaultMessage(), getJsonPath(e),
                        e.getRejectedValue()))
                .toList();

        logError(chLogger, request, "Conflicting filing data", ex, errorList);
        return new ApiErrors(errorList);
    }

    @ExceptionHandler(FilingResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiErrors handleResourceNotFoundException(final FilingResourceNotFoundException ex,
                                                     final WebRequest request) {
        final var error = new ApiError(validation.get("filing-resource-not-found"),
                getRequestURI(request),
                LocationType.RESOURCE.getValue(), ErrorType.VALIDATION.getType());

        Optional.ofNullable(ex.getMessage())
                .ifPresent(m -> error.addErrorValue("{filing-resource-id}", m));

        final var errorList = List.of(error);
        logError(chLogger, request, ex.getMessage(), ex, errorList);
        return new ApiErrors(errorList);
    }

    @ExceptionHandler({
            PscServiceException.class,
            TransactionServiceException.class,
            CompanyProfileServiceException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiErrors handleServiceException(final Exception ex,
                                            final WebRequest request) {
        final var errorList = List.of(createApiServiceError(ex, request, chLogger));
        logError(chLogger, request, ex.getMessage(), ex, errorList);
        return new ApiErrors(errorList);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull final Exception ex,
                                                             @Nullable final Object body,
                                                             @NonNull final HttpHeaders headers,
                                                             @NonNull final HttpStatusCode statusCode,
                                                             @NonNull final WebRequest request) {
        List<ApiError> errorList = null;
        if (!(ex instanceof NoResourceFoundException)) {
            errorList = List.of(createApiServiceError(ex, request, chLogger));
            logError(chLogger, request, "INTERNAL ERROR", ex, errorList);
        }

        return super.handleExceptionInternal(ex, new ApiErrors(errorList), headers, statusCode, request);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiErrors handleAllUncaughtException(final RuntimeException ex,
        final WebRequest request) {
        final var errorList = List.of(createApiServiceError(ex, request, chLogger));
        logError(chLogger, request, "Internal error", ex, errorList);
        return new ApiErrors(errorList);
    }

    private static ApiError createApiServiceError(final Exception ex,
                                                  final WebRequest request,
                                                  final Logger chLogger) {
        final var error = new ApiError("Service Unavailable: {error}",
                getRequestURI(request), LocationType.RESOURCE.getValue(),
                ErrorType.SERVICE.getType());

        Optional.ofNullable(ex)
                .filter(IllegalArgumentException.class::isInstance)
                .map(Throwable::getCause)
                .map(Throwable::getMessage)
                .map(m -> m.contains("expected numeric type"))
                .ifPresent(c -> logError(chLogger, request, "A dependent CHS service may be unavailable", ex));

        String errorMessage = "Internal server error";
        if (ex != null && ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
            errorMessage = ex.getMessage();
        }

        error.addErrorValue("error", errorMessage);

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
                .orElse("$");
    }

    private String getMismatchErrorMessage(
            final MismatchedInputException mismatchedInputException) {
        if (mismatchedInputException instanceof UnrecognizedPropertyException) {
            return validation.get("unknown-property-name");
        }
        else {
            final var message = mismatchedInputException.getMessage();
            final var parseMatcher = PARSE_MESSAGE_PATTERN.matcher(message);

            return parseMatcher.find() ? parseMatcher.group(1) : "";
        }

    }

    private String redactErrorMessage(final String s) {
        return StringUtils.substringBefore(s, ":");
    }

    private ResponseEntity<Object> createRedactedErrorResponseEntity(final RuntimeException ex,
            final WebRequest request, final Throwable cause, final String baseMessage) {
        final ApiError error;
        final String message;

        if (cause instanceof JsonProcessingException jpe) {
            final var location = jpe.getLocation();
            var jsonPath = "$";
            Object rejectedValue = null;

            if (cause instanceof MismatchedInputException mie) {
                message = getMismatchErrorMessage(mie);


                final var fieldNameOpt = ((MismatchedInputException) cause).getPath()
                        .stream()
                        .findFirst()
                        .map(JsonMappingException.Reference::getFieldName);
                jsonPath += fieldNameOpt.map(f -> "." + f)
                        .orElse("");

                if (jpe instanceof InvalidFormatException) {
                    rejectedValue = ((InvalidFormatException) cause).getValue();
                }
            }
            else {
                message = redactErrorMessage(cause.getMessage());
            }
            error = buildRequestBodyError(baseMessage + message, jsonPath, rejectedValue);
            addLocationInfo(error, location);

            if (cause instanceof UnrecognizedPropertyException unrecognized) {
                error.addErrorValue("property-name", unrecognized.getPropertyName());
            }
        }
        else {
            message = redactErrorMessage(getMostSpecificCause(ex).getMessage());
            error = buildRequestBodyError(baseMessage + message, "$", null);

        }
        logError(chLogger, request, String.format("Message not readable: %s", message), ex);
        return ResponseEntity.badRequest()
                .body(new ApiErrors(List.of(error)));
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
            .ifPresent(r -> error.addErrorValue("rejected-value", r));

        return error;
    }

    private static void logError(final Logger chLogger, final WebRequest request, final String msg,
        final Exception ex) {
        logError(chLogger, request, msg, ex, null);
    }

    private static void logError(final Logger chLogger, final WebRequest request, final String msg,
        final Exception ex,
        @Nullable final List<ApiError> apiErrorList) {
        final Map<String, Object> logMap = new HashMap<>();
        final var servletRequest = ((ServletWebRequest) request).getRequest();
        logMap.put("path", servletRequest.getRequestURI());
        logMap.put("method", servletRequest.getMethod());
        Optional.ofNullable(apiErrorList).ifPresent(l -> logMap.put("errors", l));
        chLogger.error(msg, ex, logMap);
    }

    public static Throwable getMostSpecificCause(final Throwable original) {
        final Throwable rootCause = ExceptionUtils.getRootCause(original);

        return rootCause != null ? rootCause : original;
    }

    private String getFieldErrorApiEnumerationMessage(final FieldError e) {
        final var codes = Objects.requireNonNull(e.getCodes());
        return validation.get(codes[codes.length - 1]);
    }
}
