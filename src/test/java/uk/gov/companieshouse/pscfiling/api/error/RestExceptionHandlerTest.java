package uk.gov.companieshouse.pscfiling.api.error;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.CompanyProfileServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.ConflictingFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.exception.InvalidPatchException;
import uk.gov.companieshouse.pscfiling.api.exception.MergePatchException;
import uk.gov.companieshouse.pscfiling.api.exception.PscServiceException;
import uk.gov.companieshouse.pscfiling.api.exception.TransactionServiceException;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {
    public static final String BLANK_JSON_QUOTED = "\"\"";
    public static final String MALFORMED_JSON_QUOTED = "\"{\"";
    private static final String PSC07_FRAGMENT = "{\"reference_etag\": \"etag\","
            + "\"reference_psc_id\": \"id\","
            + "\"ceased_on\": \"2022-09-13\","
            + "\"register_entry_date\": \"2022-11-12\"}";

    private RestExceptionHandler testExceptionHandler;

    @Mock
    private HttpHeaders headers;
    @Mock
    private ServletWebRequest request;
    @Mock
    private MismatchedInputException mismatchedInputException;
    @Mock
    private InvalidFormatException invalidFormatException;
    @Mock
    private UnrecognizedPropertyException unrecognizedPropertyException;
    @Mock
    private JsonParseException jsonParseException;
    @Mock
    private Logger logger;

    private MockHttpServletRequest servletRequest;

    @Mock
    private JsonMappingException.Reference mappingReference;

    private Map<String, String> validation;
    private String[] codes1;
    private String[] codes2;
    private FieldError fieldError;
    private FieldError fieldErrorWithRejectedValue;
    private ApiError expectedError;
    private ApiError expectedErrorWithRejectedValue;

    @BeforeEach
    void setUp() {
        validation = Map.of("filing-resource-not-found",
                "Filing resource {filing-resource-id} not found", "NotBlank", "field is blank",
                "PastOrPresent", "{rejected-value} is future date", "patch-merge-error-prefix",
                "Failed to merge patch request: ", "unknown-property-name",
                "Property is not recognised: {property-name}", "json-syntax-prefix",
                "JSON parse error: ");
        testExceptionHandler = new RestExceptionHandler(validation, logger);
        servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/path/to/resource");
        codes1 = new String[]{"code1", "object.addressLine1", "code3", "NotBlank"};
        codes2 = new String[]{"code1", "object.ceasedOn", "code3", "PastOrPresent"};
        fieldError = new FieldError("object", "field1", null, false, codes1, null, "error");
        fieldErrorWithRejectedValue =
                new FieldError("object", "ceasedOn", "3000-10-13", false, codes2, null,
                        "errorWithRejectedValue");
        expectedError =
                new ApiError("field is blank", "$.address_line_1", "json-path", "ch:validation");
        expectedErrorWithRejectedValue =
                new ApiError("{rejected-value} is future date", "$.ceased_on", "json-path",
                        "ch:validation");
        expectedErrorWithRejectedValue.addErrorValue("rejected-value", "3000-10-13");
    }

    @Test
    void handleHttpMessageNotReadableWhenJsonBlank() {
        when(request.getRequest()).thenReturn(servletRequest);

        final var message = new MockHttpInputMessage(BLANK_JSON_QUOTED.getBytes());
        final var exceptionMessage = new HttpMessageNotReadableException("Unexpected end-of-input: "
                + "expected close marker for Object (start marker at [Source: (org"
                + ".springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 1])\n"
                + " at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); "
                + "line: 1, column: 2]", message);

        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);
        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError("JSON parse error: Unexpected end-of-input", "$", "json-path",
                        "ch:validation");
        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError.getErrorValues(), is(nullValue()));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMessageNotReadableWhenJsonMalformed() {
        when(request.getRequest()).thenReturn(servletRequest);

        final var message = new MockHttpInputMessage(MALFORMED_JSON_QUOTED.getBytes());
        final var exceptionMessage = new HttpMessageNotReadableException("Unexpected end-of-input: "
                + "expected close marker for Object (start marker at [Source: (org"
                + ".springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 1])\n"
                + " at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); "
                + "line: 1, column: 2]", message);

        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);
        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError = new ApiError(
                "JSON parse error: Unexpected end-of-input", "$",
                "json-path", "ch:validation");

        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError.getErrorValues(), is(nullValue()));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMessageNotReadableWhenMismatchedInputException() {
        final var msg =
                "Cannot deserialize value of type `java.time.LocalDate` from String \"ABC\": "
                        + "Failed to deserialize java.time.LocalDate: (java.time.format"
                        + ".DateTimeParseException) Text 'ABC' could not be parsed at index 0";
        final var message = new MockHttpInputMessage("{]".getBytes());

        when(request.getRequest()).thenReturn(servletRequest);
        when(mismatchedInputException.getMessage()).thenReturn(msg);
        when(mismatchedInputException.getLocation()).thenReturn(new JsonLocation(null, 100, 3, 7));
        when(mismatchedInputException.getPath()).thenReturn(List.of(mappingReference));
        when(mappingReference.getFieldName()).thenReturn("ceased_on");

        final var exceptionMessage =
                new HttpMessageNotReadableException(msg, mismatchedInputException, message);
        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);
        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError("JSON parse error: Text 'ABC' could not be parsed at index 0",
                        "$.ceased_on", "json-path", "ch:validation");
        expectedError.addErrorValue("offset", "line: 3, column: 7");
        expectedError.addErrorValue("line", "3");
        expectedError.addErrorValue("column", "7");
        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMessageNotReadableWhenMismatchedInputExceptionMessageNotMatchingParsePattern() {
        final var msg =
                "Cannot deserialize value of type `java.time.LocalDate` from String \"ABC\": "
                        + "Failed to deserialize java.time.LocalDate: (java.time.format"
                        + ".DateTimeParseException)"; // Does not contain 'Text'
        final var message = new MockHttpInputMessage("{]".getBytes());

        when(request.getRequest()).thenReturn(servletRequest);
        when(mismatchedInputException.getMessage()).thenReturn(msg);
        when(mismatchedInputException.getLocation()).thenReturn(new JsonLocation(null, 100, 3, 7));
        when(mismatchedInputException.getPath()).thenReturn(List.of(mappingReference));
        when(mappingReference.getFieldName()).thenReturn("ceased_on");

        final var exceptionMessage =
                new HttpMessageNotReadableException(msg, mismatchedInputException, message);
        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);
        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError("JSON parse error: ",
                        // missing 'Text' clause not copied from exception message
                        "$.ceased_on", "json-path", "ch:validation");
        expectedError.addErrorValue("offset", "line: 3, column: 7");
        expectedError.addErrorValue("line", "3");
        expectedError.addErrorValue("column", "7");
        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMessageNotReadableWhenInvalidFormatException() {
        final var msg = "Cannot deserialize value of type `java.time.LocalDate` from String "
                + "\"2022-09-99\": Failed to deserialize java.time.LocalDate: (java.time"
                + ".format.DateTimeParseException) Text '2022-09-99' could not be parsed:"
                + " Invalid value for DayOfMonth (valid values 1 - 28/31): 99\n at "
                + "[Source: (org.springframework.util.StreamUtils$NonClosingInputStream);"
                + " line: 2, column: 18] (through reference chain: uk.gov.companieshouse"
                + ".pscfiling.api.model.dto.PscIndividualDto$Builder[\"ceased_on\"])";
        final var message = new MockHttpInputMessage(
                PSC07_FRAGMENT.replaceAll("2022-09-13", "2022-09-99").getBytes());

        when(request.getRequest()).thenReturn(servletRequest);
        when(invalidFormatException.getMessage()).thenReturn(msg);
        when(invalidFormatException.getLocation()).thenReturn(new JsonLocation(null, 100, 3, 7));
        when(invalidFormatException.getPath()).thenReturn(List.of(mappingReference));
        when(mappingReference.getFieldName()).thenReturn("ceased_on");

        final var dateParseMsg =
                "JSON parse error: Text '2022-09-99' could not be parsed: Invalid value for "
                        + "DayOfMonth (valid values 1 - 28/31): 99";
        final var exceptionMessage =
                new HttpMessageNotReadableException(dateParseMsg, invalidFormatException, message);
        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);
        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError(dateParseMsg, "$.ceased_on", "json-path", "ch:validation");
        expectedError.addErrorValue("offset", "line: 3, column: 7");
        expectedError.addErrorValue("line", "3");
        expectedError.addErrorValue("column", "7");
        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMessageNotReadableWhenUnrecognizedPropertyException() {
        final var message = new MockHttpInputMessage(
                PSC07_FRAGMENT.replaceAll("ceased_on", "ceased_onX").getBytes());
        when(request.getRequest()).thenReturn(servletRequest);
        when(unrecognizedPropertyException.getLocation()).thenReturn(
                new JsonLocation(null, 100, 3, 7));
        when(unrecognizedPropertyException.getPath()).thenReturn(List.of(mappingReference));
        when(unrecognizedPropertyException.getPropertyName()).thenReturn("ceased_onX");
        when(mappingReference.getFieldName()).thenReturn("ceased_onX");
        final var unrecognizedMsg = "JSON parse error: Property is not recognised: {property-name}";
        final var exceptionMessage =
                new HttpMessageNotReadableException(unrecognizedMsg, unrecognizedPropertyException,
                        message);

        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);

        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError(unrecognizedMsg, "$.ceased_onX", "json-path", "ch:validation");
        expectedError.addErrorValue("offset", "line: 3, column: 7");
        expectedError.addErrorValue("line", "3");
        expectedError.addErrorValue("column", "7");
        expectedError.addErrorValue("property-name", "ceased_onX");
        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError.getError(), is(expectedError.getError()));
        assertThat(actualError.getLocation(), is(expectedError.getLocation()));
        assertThat(actualError.getType(), is(expectedError.getType()));
        assertThat(actualError.getLocationType(), is(expectedError.getLocationType()));
        assertThat(actualError.getErrorValues(), is(expectedError.getErrorValues()));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMessageNotReadableWhenJsonParseException() {
        final var msg = "JsonParseException";
        final var message =
                new MockHttpInputMessage(PSC07_FRAGMENT.replaceAll("2022", "ABC").getBytes());

        when(request.getRequest()).thenReturn(servletRequest);
        when(jsonParseException.getMessage()).thenReturn(msg);
        when(jsonParseException.getLocation()).thenReturn(new JsonLocation(null, 100, 3, 7));

        final var exceptionMessage =
                new HttpMessageNotReadableException(msg, jsonParseException, message);

        final var response =
                testExceptionHandler.handleHttpMessageNotReadable(exceptionMessage, headers,
                        HttpStatus.BAD_REQUEST, request);

        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError("JSON parse error: " + msg, "$", "json-path", "ch:validation");
        expectedError.addErrorValue("offset", "line: 3, column: 7");
        expectedError.addErrorValue("line", "3");
        expectedError.addErrorValue("column", "7");
        final var actualError = Objects.requireNonNull(apiErrors).getErrors().iterator().next();

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actualError, is(samePropertyValuesAs(expectedError)));
    }

    @Test
    void handleHttpMediaTypeNotSupported() {
        final var mediaMergePatch = MediaType.parseMediaType("application/merge-patch+json");
        final var exception = new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_PDF,
                List.of(MediaType.APPLICATION_JSON, mediaMergePatch));

        when(request.getRequest()).thenReturn(servletRequest);

        final var response =
                testExceptionHandler.handleHttpMediaTypeNotSupported(exception, headers,
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);

        assertThat(response.getStatusCode(), is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
        assertThat(response.getHeaders().getAcceptPatch(), contains(mediaMergePatch));
        assertThat(response.getHeaders().get("Accept-Post"),
                contains(MediaType.APPLICATION_JSON.toString()));
    }

    @Test
    void handleResourceNotFoundException() {
        final var exception = new FilingResourceNotFoundException(
                "Filing resource {filing-resource-id} not found");

        when(request.getRequest()).thenReturn(servletRequest);

        final var apiErrors = testExceptionHandler.handleResourceNotFoundException(exception,
                request);
        final var expectedError = new ApiError("Filing resource {filing-resource-id} not found",
                null, "resource", "ch:validation");

        expectedError.addErrorValue("{filing-resource-id}",
                "Filing resource {filing-resource-id} not found");

        assertThat(apiErrors.getErrors(), contains(expectedError));
    }

    @Test
    void handleInvalidFilingException() {
        when(request.getRequest()).thenReturn(servletRequest);
        final var exception =
                new InvalidFilingException(List.of(fieldError, fieldErrorWithRejectedValue));

        final var apiErrors = testExceptionHandler.handleInvalidFilingException(exception, request);

        assertThat(apiErrors.getErrors(), hasSize(2));
        assertThat(apiErrors.getErrors(),
                containsInAnyOrder(expectedError, expectedErrorWithRejectedValue));
    }

    @Test
    void handleConflictingFilingException() {
        when(request.getRequest()).thenReturn(servletRequest);
        final var exception =
                new ConflictingFilingException(List.of(fieldError, fieldErrorWithRejectedValue));

        final var apiErrors =
                testExceptionHandler.handleConflictingFilingException(exception, request);

        assertThat(apiErrors.getErrors(), hasSize(2));
        assertThat(apiErrors.getErrors(),
                containsInAnyOrder(expectedError, expectedErrorWithRejectedValue));
    }

    @ParameterizedTest(name = "[{index}]: cause={0}")
    @MethodSource("causeProvider")
    void handleServiceException(final Exception exception) {
        when(request.getRequest()).thenReturn(servletRequest);
        when(request.resolveReference("request")).thenReturn(servletRequest);

        final var apiErrors = testExceptionHandler.handleServiceException(exception, request);
        final var expectedError =
                new ApiError(exception.getMessage(), "/path/to/resource", "resource", "ch:service");

        Optional.ofNullable(exception.getCause())
                .ifPresent(e -> expectedError.addErrorValue("cause", e.getMessage()));

        assertThat(apiErrors.getErrors(), contains(expectedError));
    }

    @Test
    void handleExceptionInternal() {
        final var exception = new NullPointerException("test");
        final Object body = 0;

        when(request.getRequest()).thenReturn(servletRequest);
        when(request.resolveReference("request")).thenReturn(servletRequest);

        final var response =
                testExceptionHandler.handleExceptionInternal(exception, body, new HttpHeaders(),
                        HttpStatus.INTERNAL_SERVER_ERROR, request);

        final var apiErrors = (ApiErrors) response.getBody();
        final var expectedError =
                new ApiError("test", "/path/to/resource", "resource", "ch:service");

        assertThat(apiErrors, is(notNullValue()));
        assertThat(apiErrors.getErrors(), contains(expectedError));
    }

    @ParameterizedTest(name = "[{index}]: cause={0}")
    @NullSource
    @MethodSource("causeProvider")
    void handleAllUncaughtException(final Exception cause) {
        final var exception = new RuntimeException("test", cause);

        when(request.getRequest()).thenReturn(servletRequest);
        when(request.resolveReference("request")).thenReturn(servletRequest);

        final var apiErrors = testExceptionHandler.handleAllUncaughtException(exception, request);

        final var expectedError =
                new ApiError("test", "/path/to/resource", "resource", "ch:service");

        if (cause != null) {
            expectedError.addErrorValue("cause", cause.getMessage());
        }
        assertThat(apiErrors.getErrors(), contains(expectedError));
    }

    private static Stream<Arguments> causeProvider() {
        final var cause = new ArithmeticException("DIV/0");

        return Stream.of(Arguments.of(new PscServiceException("PSCServiceException", cause)),
                Arguments.of(new TransactionServiceException("TransactionServiceException", cause)),
                Arguments.of(new CompanyProfileServiceException("CompanyProfileServiceException",
                        cause)));
    }

    @Test
    void handleMergePatchException() {
        when(request.getRequest()).thenReturn(servletRequest);

        final var exception = new MergePatchException(unrecognizedPropertyException);

        when(unrecognizedPropertyException.getPropertyName()).thenReturn("field");
        when(unrecognizedPropertyException.getLocation()).thenReturn(
                new JsonLocation(null, 100, 3, 7));
        when(unrecognizedPropertyException.getPath()).thenReturn(List.of(mappingReference));
        when(mappingReference.getFieldName()).thenReturn("field");

        final var apiErrors = testExceptionHandler.handleMergePatchException(exception, request);

        final var expectedError = new ApiError(
                "Failed to merge patch request: Property is not recognised: {property-name}",
                "$.field", "json-path", "ch:validation");
        expectedError.addErrorValue("offset", "line: 3, column: 7");
        expectedError.addErrorValue("line", "3");
        expectedError.addErrorValue("column", "7");
        expectedError.addErrorValue("property-name", "field");

        assertThat(((ApiErrors) Objects.requireNonNull(apiErrors.getBody())).getErrors(),
                contains(expectedError));
    }


    @Test
    void getMostSpecificCauseWhenPresent() {
        final var cause = new InvalidPatchException(Collections.emptyList());

        final var exception = new MergePatchException(cause);

        assertThat(RestExceptionHandler.getMostSpecificCause(exception), is(cause));
    }

    @Test
    void getMostSpecificCauseWhenNotPresent() {
        final Throwable exception = null;

        assertThat(RestExceptionHandler.getMostSpecificCause(exception), is(exception));
    }
}