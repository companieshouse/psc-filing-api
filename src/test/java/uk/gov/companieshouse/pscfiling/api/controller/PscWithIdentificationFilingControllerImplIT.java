package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
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

import java.net.URI;
import java.time.Clock;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.mapper.PscMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;

@Tag("web")
@Import(PscExistsValidator.class)
@WebMvcTest(controllers = PscWithIdentificationFilingControllerImpl.class)
class PscWithIdentificationFilingControllerImplIT extends BaseControllerIT {
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private PscApi pscDetails;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private PscMapper filingMapper;
    @MockBean
    private Clock clock;
    @MockBean
    private Logger logger;

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
        final var dto = PscWithIdentificationDto.builder().referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var filing = PscWithIdentificationFiling.builder().referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var locationUri = UriComponentsBuilder.fromPath("/")
                .pathSegment("transactions", TRANS_ID,
                        "persons-with-significant-control/corporate-entity", FILING_ID)
                .build();

        when(filingMapper.map(dto)).thenReturn(filing);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.CORPORATE_ENTITY,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscFilingService.save(any(PscWithIdentificationFiling.class), eq(TRANS_ID))).thenReturn(
                        PscWithIdentificationFiling.builder(filing).id(FILING_ID)
                                .build()) // copy of 'filing' with id=FILING_ID
                .thenAnswer(i -> PscWithIdentificationFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(body)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", locationUri.toUriString()))
                .andExpect(jsonPath("$.id", is(FILING_ID)))
                .andExpect(jsonPath("$.register_entry_date", is(REGISTER_ENTRY_DATE.toString())))
                .andExpect(jsonPath("$.country_of_residence").doesNotExist());
        verify(filingMapper).map(dto);
    }

    @Test
    void createFilingWhenRequestBodyMissingThenResponse400() throws Exception {
        final var expectedError = createExpectedValidationError(
                "Required request body is missing: public org.springframework.http"
                        + ".ResponseEntity<java.lang.Object> uk.gov.companieshouse.pscfiling.api"
                        + ".controller.PscWithIdentificationFilingControllerImpl.createFiling(java.lang.String,uk"
                        + ".gov.companieshouse.pscfiling.api.model.PscTypeConstants,uk.gov"
                        + ".companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto,org"
                        + ".springframework.validation.BindingResult,javax.servlet.http"
                        + ".HttpServletRequest)", "$", 1, 1);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content("")
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
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
    void createFilingWhenRequestBodyBlankThenResponse400() throws Exception {
        final var expectedError = createExpectedValidationError(
                "Cannot coerce empty String (\"\") to `uk.gov.companieshouse.pscfiling.api.model"
                        + ".dto"
                        + ".PscWithIdentificationDto$Builder` value (but could if coercion was enabled "
                        + "using "
                        + "`CoercionConfig`)\n"
                        + " at [Source: (org.springframework.util"
                        + ".StreamUtils$NonClosingInputStream); line: 1, "
                        + "column: 1]", "$", 1, 1);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(EMPTY_QUOTED_JSON)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
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

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(MALFORMED_JSON)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
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
                        + ".StreamUtils$NonClosingInputStream); line: 1, column: 173]", "$", 1,
                173);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content("{" + PSC07_FRAGMENT)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Unexpected end-of-input")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 173"), hasEntry("line", "1"),
                                hasEntry("column", "173"))));
    }

    @Test
    void createFilingWhenCeasedOnDateUnparseableThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "ABC") + "}";
        final var expectedError =
                createExpectedValidationError("JSON parse error:", "$.ceased_on", 1, 125);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(body)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Text 'ABC' could not be parsed at index 0")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 125"), hasEntry("line", "1"),
                                hasEntry("column", "125"))));
    }

    @Test
    void createFilingWhenCeasedOnDateOutsideRangeThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "2022-09-99") + "}";
        final var expectedError =
                createExpectedValidationError("JSON parse error:", "$.ceased_on", 1, 125);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(body)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Text '2022-09-99' could not be parsed: Invalid "
                                + "value for DayOfMonth (valid values 1 - 28/31): 99")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 125"), hasEntry("line", "1"),
                                hasEntry("column", "125"))));
    }

    @Test
    void createFilingWhenCeasedOnDateInvalidThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "2022-11-31") + "}";
        final var expectedError =
                createExpectedValidationError("JSON parse error:", "$.ceased_on", 1, 125);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(body)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error",
                        is("JSON parse error: Text '2022-11-31' could not be parsed: Invalid date"
                                + " 'NOVEMBER 31'")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 125"), hasEntry("line", "1"),
                                hasEntry("column", "125"))));
    }

    @Test
    void createFilingWhenCeasedOnDateFutureThenResponse400() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "3000-09-13") + "}";
        final var expectedError =
                createExpectedValidationError("must be a date in the past or in the present",
                        "$.ceased_on", 1, 75);

        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(body)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("location"))
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
    void createFilingWhenCeasedOnDateBlankThenResponse201() throws Exception {
        final var body = "{" + PSC07_FRAGMENT.replace("2022-09-13", "") + "}";
        final var dto = PscWithIdentificationDto.builder().referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(null)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var filing = PscWithIdentificationFiling.builder().referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(null)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var locationUri = UriComponentsBuilder.fromPath("/")
                .pathSegment("transactions", TRANS_ID,
                        "persons-with-significant-control/corporate-entity", FILING_ID)
                .build();

        when(filingMapper.map(dto)).thenReturn(filing);
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getName()).thenReturn("Mr Joe Bloggs");
        when(pscFilingService.save(any(PscWithIdentificationFiling.class), eq(TRANS_ID))).thenReturn(
                        PscWithIdentificationFiling.builder(filing).id(FILING_ID)
                                .build()) // copy of 'filing' with id=FILING_ID
                .thenAnswer(i -> PscWithIdentificationFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        mockMvc.perform(post(URL_PSC_CORPORATE_ENTITY, TRANS_ID).content(body)
                        .requestAttr("transaction", transaction)
                        .contentType(APPLICATION_JSON)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", locationUri.toUriString()))
                .andExpect(jsonPath("$.id", is(FILING_ID)))
                .andExpect(jsonPath("$.register_entry_date", is(REGISTER_ENTRY_DATE.toString())));
        verify(filingMapper).map(dto);
    }

    @Test
    void getFilingForReviewThenResponse200() throws Exception {
        final var dto = PscWithIdentificationDto.builder().referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(CEASED_ON_DATE)
                .build();

                final Links links = new Links(new URI("/transactions/" + TRANS_ID +
                "/persons-with-significant-control/corporate-entity/" + FILING_ID),
                                        new URI("validation_status"));

        final var filing = PscWithIdentificationFiling.builder()
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(CEASED_ON_DATE)
                .links(links)
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(filingMapper.map((PscCommunal) filing)).thenReturn(dto);

        mockMvc.perform(get(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference_etag", is(ETAG)))
                .andExpect(jsonPath("$.reference_psc_id", is(PSC_ID)))
                .andExpect(jsonPath("$.ceased_on", is(CEASED_ON_DATE.toString())));
    }

    @Test
    void getFilingForReviewNotFoundThenResponse404() throws Exception {

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
                        get(URL_PSC_CORPORATE_ENTITY + "/{filingId}", TRANS_ID, FILING_ID).headers(httpHeaders))
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

}