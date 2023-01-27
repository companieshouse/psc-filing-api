package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.answerVoid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.error.ApiErrorResponse;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;

@Tag("web")
@Import(PscExistsValidator.class)
@WebMvcTest(controllers = PscFilingControllerImpl.class)
class PscFilingControllerImplIT extends BaseControllerIT {
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private FilingValidationService filingValidationService;
    @MockBean
    private PscApi pscDetails;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private PscIndividualMapper filingMapper;
    @MockBean
    private Clock clock;
    @MockBean
    private Logger logger;

    @Mock
    private ApiErrorResponseException errorResponseException;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() throws Exception {
        super.setUp();
    }

    @Test
    @DisplayName("Test related to bug PSC-118")
    void expectNotFoundResponseWhenPscTypeInvalid() throws Exception {
        final var body = "{" + PSC07_FRAGMENT + "}";

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/invalid",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void createFilingWhenPSC07PayloadOKThenResponse201() throws Exception {
        final var body = "{" + PSC07_FRAGMENT + "}";
        final var dto = PscIndividualDto.builder()
                .referenceEtag("etag")
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var filing = PscIndividualFiling.builder()
                .referenceEtag("etag")
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var locationUri = UriComponentsBuilder.fromPath("/")
                .pathSegment("transactions", TRANS_ID,
                        "persons-with-significant-control/individual", FILING_ID)
                .build();

        when(filingMapper.map(dto)).thenReturn(filing);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PSC_TYPE,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getName()).thenReturn("Mr Joe Bloggs");
        when(pscFilingService.save(any(PscIndividualFiling.class), eq(TRANS_ID))).thenReturn(
                        PscIndividualFiling.builder(filing).id(FILING_ID)
                                .build()) // copy of 'filing' with id=FILING_ID
                .thenAnswer(i -> PscIndividualFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", locationUri.toUriString()))
                .andExpect(jsonPath("$").doesNotExist());
        verify(filingMapper).map(dto);
    }

    @Test
    void createFilingWhenRequestBodyMissingThenResponse400() throws Exception {
        final var expectedError = createExpectedValidationError(
                "Required request body is missing: public org.springframework.http"
                        + ".ResponseEntity<java.lang.Object> uk.gov.companieshouse.pscfiling.api"
                        + ".controller.PscFilingControllerImpl.createFiling(java.lang.String,uk"
                        + ".gov.companieshouse.pscfiling.api.model.PscTypeConstants,uk.gov"
                        + ".companieshouse.pscfiling.api.model.dto.PscIndividualDto,org"
                        + ".springframework.validation.BindingResult,javax.servlet.http"
                        + ".HttpServletRequest)", "$", 1, 1);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content("").contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Required request body is missing")))
                .andExpect(jsonPath("$.errors[0].error_values").doesNotExist());
    }

    @Test
    void createFilingWhenPscNotFoundThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT + "}";
        final var pscServiceError =
                createExpectedApiError("PSC Details not found for " + PSC_ID + ": 404 Not Found",
                        "$.reference_psc_id",
                        LocationType.JSON_PATH, ErrorType.VALIDATION);
        final var pscServiceResponse = new ApiErrorResponse();

        pscServiceResponse.setErrors(List.of(pscServiceError));
        when(errorResponseException.getDetails()).thenReturn(pscServiceResponse);
        when(errorResponseException.getMessage()).thenReturn("404 Not Found\\n{\"errors"
                + "\":[{\"type\":\"ch:service\",\"error\":\"company-psc-details-not-found\"}]}");

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PSC_TYPE,
                PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("PSC Details not found for " + PSC_ID + ": 404 Not Found",
                        errorResponseException));

        final var bindingErrorCodes = new String[]{null, "notFound.reference_psc_id"};
        final var fieldErrorWithRejectedValue =
                new FieldError("object", "reference_psc_id", PSC_ID, false, bindingErrorCodes, null,
                        "PSC with that reference ID was not found");

        doAnswer(answerVoid((FilingValidationContext c) -> c.getErrors().add(
                fieldErrorWithRejectedValue))).when(filingValidationService)
                .validate(any(FilingValidationContext.class));

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", pscServiceError.getLocation()),
                                hasEntry("location_type", pscServiceError.getLocationType()),
                                hasEntry("type", pscServiceError.getType()),
                                hasEntry("error", fieldErrorWithRejectedValue.getDefaultMessage()))))
                .andExpect(jsonPath("$.errors[0].error_values", hasEntry("rejected", PSC_ID)));
    }

    @Test
    void createFilingWhenRequestBodyBlankThenResponse400() throws Exception {
        final var expectedError = createExpectedValidationError(
                "Cannot coerce empty String (\"\") to `uk.gov.companieshouse.pscfiling.api.model"
                        + ".dto"
                        + ".PscIndividualDto$Builder` value (but could if coercion was enabled "
                        + "using "
                        + "`CoercionConfig`)\n"
                        + " at [Source: (org.springframework.util"
                        + ".StreamUtils$NonClosingInputStream); line: 1, "
                        + "column: 1]", "$", 1, 1);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(EMPTY_QUOTED_JSON)
                        .contentType("application/json")
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error", is("JSON parse error: ")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 1"), hasEntry("line", "1"),
                                hasEntry("column", "1"))));
    }

    @Test
    void createFilingWhenRequestBodyMalformedThenResponse400() throws Exception {
        final var expectedError = createExpectedValidationError("Unexpected end-of-input: "
                + "expected close marker for Object (start marker at [Source: (org"
                + ".springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 2])\n"
                + " at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); "
                + "line: 1, column: 2]", "$", 1, 1);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(MALFORMED_JSON)
                        .contentType("application/json")
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Unexpected end-of-input")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 2"), hasEntry("line", "1"),
                                hasEntry("column", "2"))));
    }

    @Test
    void createFilingWhenRequestBodyIncompleteThenResponse400() throws Exception {
        final var expectedError = createExpectedValidationError(
                "JSON parse error: Unexpected end-of-input: expected close marker for Object "
                        + "(start marker at [Source: (org.springframework.util"
                        + ".StreamUtils$NonClosingInputStream); line: 1, column: 1])\n"
                        + " at [Source: (org.springframework.util"
                        + ".StreamUtils$NonClosingInputStream); line: 1, column: 87]", "$", 1, 87);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content("{" + PSC07_FRAGMENT)
                        .contentType("application/json")
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Unexpected end-of-input")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 138"), hasEntry("line", "1"),
                                hasEntry("column", "138"))));
    }

    @Test
    void createFilingWhenCeasedOnDateUnparseableThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "ABC") + "}";
        final var expectedError =
                createExpectedValidationError("JSON parse error:", "$.ceased_on", 1, 75);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Text 'ABC' could not be parsed at index 0")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 90"), hasEntry("line", "1"),
                                hasEntry("column", "90"))));
    }

    @Test
    void createFilingWhenCeasedOnDateOutsideRangeThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "2022-09-99") + "}";
        final var expectedError =
                createExpectedValidationError("JSON parse error:", "$.ceased_on", 1, 75);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Text '2022-09-99' could not be parsed: Invalid "
                                + "value for DayOfMonth (valid values 1 - 28/31): 99")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 90"), hasEntry("line", "1"),
                                hasEntry("column", "90"))));
    }

    @Test
    void createFilingWhenCeasedOnDateInvalidThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "2022-11-31") + "}";
        final var expectedError =
                createExpectedValidationError("JSON parse error:", "$.ceased_on", 1, 75);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Text '2022-11-31' could not be parsed: Invalid date"
                                + " 'NOVEMBER 31'")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 90"), hasEntry("line", "1"),
                                hasEntry("column", "90"))));
    }

    @Test
    void createFilingWhenCeasedOnDateFutureThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "3000-09-13") + "}";
        final var expectedError =
                createExpectedValidationError("must be a date in the past or in the present",
                        "$.ceased_on", 1, 75);

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        containsString("must be a date in the past or in the present")))
                .andExpect(
                        jsonPath("$.errors[0].error_values", hasEntry("rejected", "3000-09-13")));
    }

    @Test
    void createFilingWhenCeasedOnDateBlankThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "") + "}";
        final var expectedError =
                createExpectedValidationError("must not be null", "$.ceased_on", 1, 75);

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error", containsString("must not be null")))
                .andExpect(jsonPath("$.errors[0].error_values", is(nullValue())));
    }

    @Test
    void getFilingForReviewThenResponse200() throws Exception {
        final var dto = PscIndividualDto.builder()
                .referenceEtag("etag")
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(CEASED_ON_DATE)
                .build();
        final var filing = PscIndividualFiling.builder()
                .referenceEtag("etag")
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(CEASED_ON_DATE)
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        when(filingMapper.map(filing)).thenReturn(dto);

        mockMvc.perform(get("/transactions/{id}/persons-with-significant-control/individual"
                        + "/{filingId}", TRANS_ID, FILING_ID).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference_etag", is("etag")))
                .andExpect(jsonPath("$.reference_psc_id", is(PSC_ID)))
                .andExpect(jsonPath("$.ceased_on", is("2022-09-13")));
    }

    @Test
    void getFilingForReviewNotFoundThenResponse404() throws Exception {

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/transactions/{id}/persons-with-significant-control/individual"
                        + "/{filingId}", TRANS_ID, FILING_ID).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private ApiError createExpectedValidationError(final String msg, final String location,
            final int line, final int column) {
        final var expectedError = new ApiError(msg, location, "json-path", "ch:validation");

        expectedError.addErrorValue("offset", String.format("line: %d, column: %d", line, column));
        expectedError.addErrorValue("line", String.valueOf(line));
        expectedError.addErrorValue("column", String.valueOf(column));

        return expectedError;
    }

    private ApiError createExpectedApiError(final String msg, final String location,
            final LocationType locationType, final ErrorType errorType) {
        return new ApiError(msg, location, locationType.getValue(), errorType.getType());
    }

}