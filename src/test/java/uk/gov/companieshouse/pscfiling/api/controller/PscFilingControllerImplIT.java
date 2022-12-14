package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.error.ApiErrorResponse;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("web")
@WebMvcTest(controllers = PscFilingControllerImpl.class)
class PscFilingControllerImplIT {
    private static final String TRANS_ID = "4f56fdf78b357bfc";
    private static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    private static final PscTypeConstants PSC_TYPE = PscTypeConstants.INDIVIDUAL;
    private static final String PASSTHROUGH_HEADER = "passthrough";
    private static final String PSC07_FRAGMENT = "\"reference_etag\": \"etag\","
            + "\"reference_psc_id\": \"id\","
            + "\"ceased_on\": \"2022-09-13\","
            + "\"register_entry_date\": \"2022-09-09\"";
    private static final String EMPTY_QUOTED_JSON = "\"\"";
    private static final String MALFORMED_JSON = "{";
    private static final Instant FIRST_INSTANT = Instant.parse("2022-10-15T09:44:08.108Z");
    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private static final String COMPANY_NUMBER = "012345678";

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private PscDetailsService pscDetailsService;
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

    private HttpHeaders httpHeaders;

    @Mock
    private ApiErrorResponseException errorResponseException;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add("ERIC-Access-Token", PASSTHROUGH_HEADER);
    }

    @Test
    void createFilingWhenPSC07PayloadOKThenResponse201() throws Exception {
        final var body = "{" + PSC07_FRAGMENT + "}";
        final var transaction = new Transaction();
        final var dto = PscIndividualDto.builder()
                .referenceEtag("etag")
                .referencePscId("id")
                .ceasedOn(LocalDate.of(2022, 9, 13))
                .registerEntryDate(LocalDate.of(2022, 9, 9))
                .build();
        final var filing = PscIndividualFiling.builder()
                .referenceEtag("etag")
                .referencePscId("id")
                .ceasedOn(LocalDate.of(2022, 9, 13))
                .registerEntryDate(LocalDate.of(2022, 9, 9))
                .build();
        final var locationUri = UriComponentsBuilder.fromPath("/")
                .pathSegment("transactions", TRANS_ID,
                        "persons-with-significant-control/individual", FILING_ID)
                .build();

        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber(COMPANY_NUMBER);

        when(filingMapper.map(dto)).thenReturn(filing);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PSC_TYPE,
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
        final var expectedError =
                createExpectedApiError("PSC Details not found for " + PSC_ID + "=: 404 Not Found",
                        "$.reference_psc_id",
                        LocationType.JSON_PATH, ErrorType.VALIDATION);
        final var errorResponse = new ApiErrorResponse();

        errorResponse.setErrors(List.of(expectedError));
        when(errorResponseException.getDetails()).thenReturn(errorResponse);
        when(errorResponseException.getMessage()).thenReturn("404 Not Found\\n{\"errors"
                + "\":[{\"type\":\"ch:service\",\"error\":\"company-psc-details-not-found\"}]}");

        final var transaction = new Transaction();

        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber(COMPANY_NUMBER);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, "id", PSC_TYPE,
                PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("PSC Details not found for " + PSC_ID + "=: 404 Not Found",
                        errorResponseException));

        mockMvc.perform(post("/transactions/{id}/persons-with-significant-control/individual",
                        TRANS_ID).content(body).contentType("application/json").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()),
                                hasEntry("error", expectedError.getError()))))
                .andExpect(jsonPath("$.errors[0].error_values", hasEntry("rejected", "id")));
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
                        allOf(hasEntry("offset", "line: 1, column: 113"), hasEntry("line", "1"),
                                hasEntry("column", "113"))));
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
                        allOf(hasEntry("offset", "line: 1, column: 65"), hasEntry("line", "1"),
                                hasEntry("column", "65"))));
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
                        allOf(hasEntry("offset", "line: 1, column: 65"), hasEntry("line", "1"),
                                hasEntry("column", "65"))));
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
                        allOf(hasEntry("offset", "line: 1, column: 65"), hasEntry("line", "1"),
                                hasEntry("column", "65"))));
    }

    @Test
    void createFilingWhenCeasedOnDateFutureThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "3000-09-13") + "}";
        final var expectedError =
                createExpectedValidationError("must be a date in the past or in the present",
                        "$.ceased_on", 1, 75);

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
                .referencePscId("id")
                .ceasedOn(LocalDate.of(2022, 9, 13))
                .registerEntryDate(LocalDate.of(2022, 9, 13))
                .build();
        final var filing = PscIndividualFiling.builder()
                .referenceEtag("etag")
                .referencePscId("id")
                .ceasedOn(LocalDate.of(2022, 9, 13))
                .registerEntryDate(LocalDate.of(2022, 9, 13))
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));

        when(filingMapper.map(filing)).thenReturn(dto);

        mockMvc.perform(get("/transactions/{id}/persons-with-significant-control/individual"
                        + "/{filingId}", TRANS_ID, FILING_ID).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference_etag", is("etag")))
                .andExpect(jsonPath("$.reference_psc_id", is("id")))
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