package uk.gov.companieshouse.pscfiling.api.controller.impl;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.PatchServiceProperties;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.model.entity.Identification;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.NaturesOfControlList;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscWithIdentificationFilingRepository;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("app")
@SpringBootTest
@AutoConfigureMockMvc
class PscWithIdentificationFilingControllerImplMergeIT extends BaseControllerIT {
    private static final URI SELF_URI = URI.create("/path/to/self");
    private static final URI VALIDATION_URI = URI.create("/path/to/self/validation");
    private static final String CORPORATE_NAME = "corporate name";
    private Identification identification;
    private NaturesOfControlList naturesOfControl;
    private Links links;
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
    private PscWithIdentificationFilingRepository individualFilingRepository;
    @MockBean
    private PatchServiceProperties patchServiceProperties;
    @MockBean
    private Clock clock;
    @MockBean
    private Logger logger;

    @Mock
    private ApiErrorResponseException errorResponseException;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        baseSetUp();
        identification = Identification.builder()
                .countryRegistered("country")
                .placeRegistered("place")
                .legalAuthority("authority")
                .legalForm("form")
                .registrationNumber("regNo")
                .build();
        links = new Links(SELF_URI, VALIDATION_URI);
        naturesOfControl = new NaturesOfControlList(List.of("type1", "type2", "type3"));
        when(patchServiceProperties.getMaxRetries()).thenReturn(1);
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

    @Test
    @DisplayName("Expect to update or replace existing fields that are not read only")
    void updateFilingWhenReplacingFields() throws Exception {
        final var body = "{\n"
                + " \"id\": \"unauthorised\",\n"
                + " \"created_at\": \""
                + SECOND_INSTANT
                + "\",\n"
                + " \"updated_at\": \""
                + FIRST_INSTANT
                + "\",\n"
                + "  \"ceased_on\": \"2022-03-03\",\n"
                + "  \"identification\": {\n"
                + "    \"country_registered\": \"Replaced\"\n"
                + "  },\n"
                + " \"natures_of_control\": [\n"
                + "    \"type4\"\n"
                + "  ]\n"
                + "}";
        final var filing = PscWithIdentificationFiling.builder()
                .id(FILING_ID)
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .createdAt(FIRST_INSTANT)
                .updatedAt(FIRST_INSTANT)
                .ceasedOn(CEASED_ON_DATE)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .name(CORPORATE_NAME)
                .naturesOfControl(naturesOfControl)
                .links(links)
                .identification(identification)
                .build();
        final var expectedIdentification =
                Identification.builder(identification).countryRegistered("Replaced").build();
        final var expectedFiling =
                PscWithIdentificationFiling.builder(filing).updatedAt(SECOND_INSTANT).ceasedOn(
                        LocalDate.of(2022, 3, 3)).identification(
                        expectedIdentification).naturesOfControl(List.of("type4")).build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(pscFilingService.save(
                any(PscWithIdentificationFiling.class))).thenAnswer(
                i -> PscWithIdentificationFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(SECOND_INSTANT);
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(
                Optional.ofNullable(expectedFiling));

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                        .contentType(APPLICATION_JSON_MERGE_PATCH)
                        .requestAttr("transaction", transaction)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(FILING_ID)))
                .andExpect(jsonPath("$.ceased_on", is("2022-03-03")))
                .andExpect(jsonPath("$.identification.place_registered", is("place")))
                .andExpect(jsonPath("$.identification.legal_authority", is("authority")))
                .andExpect(jsonPath("$.identification.legal_form", is("form")))
                .andExpect(jsonPath("$.identification.registration_number", is("regNo")))
                .andExpect(jsonPath("$.identification.country_registered", is("Replaced")))
                .andExpect(jsonPath("$.natures_of_control", containsInAnyOrder("type4")))
                .andExpect(jsonPath("$.updated_at", is(SECOND_INSTANT.toString())))
                .andExpect(jsonPath("$.created_at", is(FIRST_INSTANT.toString())));
    }

    @Test
    @DisplayName("Expect to add new fields to existing filing")
    void updateFilingWhenAddingFields() throws Exception {
        final var body = "{\n"
                + "  \"ceased_on\": \"2022-10-05\",\n"
                + "  \"name\": \""+ CORPORATE_NAME + "\",\n"
                + "  \"identification\": {\n"
                + "    \"country_registered\": \"Added\"\n"
                + "  },\n"
                + " \"natures_of_control\": [\n"
                + "    \"type1\",\n"
                + "    \"type2\",\n"
                + "    \"type3\",\n"
                + "    \"type4\"\n"
                + "  ]\n"
                + "}";
        final var filing = PscWithIdentificationFiling.builder()
                .id(FILING_ID)
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .links(links)
                .build();
        final var expectedIdentification =
                Identification.builder().countryRegistered("Added").build();
        final var expectedFiling = PscWithIdentificationFiling.builder(filing).updatedAt(
                SECOND_INSTANT).name(CORPORATE_NAME).ceasedOn(
                LocalDate.of(2022, 10, 5)).identification(expectedIdentification).naturesOfControl(
                List.of("type1", "type2", "type3", "type4")).build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(pscFilingService.save(any(PscWithIdentificationFiling.class))).thenAnswer(
                i -> PscWithIdentificationFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(SECOND_INSTANT);
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(
                Optional.ofNullable(expectedFiling));

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                        .contentType(APPLICATION_JSON_MERGE_PATCH)
                        .requestAttr("transaction", transaction)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(FILING_ID)))
                .andExpect(jsonPath("$.ceased_on", is(CEASED_ON)))
                .andExpect(jsonPath("$.name", is(CORPORATE_NAME)))
                .andExpect(jsonPath("$.identification.place_registered").doesNotExist())
                .andExpect(jsonPath("$.identification.legal_authority").doesNotExist())
                .andExpect(jsonPath("$.identification.legal_form").doesNotExist())
                .andExpect(jsonPath("$.identification.registration_number").doesNotExist())
                .andExpect(jsonPath("$.identification.country_registered", is("Added")))
                .andExpect(jsonPath("$.natures_of_control",
                        containsInAnyOrder("type1", "type2", "type3", "type4")))
                .andExpect(jsonPath("$.updated_at", is(SECOND_INSTANT.toString())));
    }

    @Test
    @DisplayName(
            "Expected: top level and nested fields are deleted with 'null' and read only fields "
                    + "are unchanged")
    void updateFilingWhenDeletingFields() throws Exception {
        final var body = "{\n"
                + " \"id\": null,\n"
                + "  \"ceased_on\": null,\n"
                + "  \"name\": null,\n"
                + "  \"identification\": {\n"
                + "    \"country_registered\": null\n"
                + "  },\n"
                + " \"links\": {\n"
                + "    \"self\": null\n"
                + "  },\n"
                + " \"natures_of_control\": [\n"
                + "  ]\n"
                + "}";
        final var filing = PscWithIdentificationFiling.builder()
                .id(FILING_ID)
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .name(CORPORATE_NAME)
                .identification(identification)
                .links(links)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();
        final var expectedIdentification =
                Identification.builder(identification).countryRegistered(null).build();
        final var expectedFiling = PscWithIdentificationFiling.builder(filing).updatedAt(
                SECOND_INSTANT).name(null).ceasedOn(null).identification(
                expectedIdentification).naturesOfControl(Collections.emptyList()).build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(pscFilingService.save(any(PscWithIdentificationFiling.class))).thenAnswer(
                i -> PscWithIdentificationFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(SECOND_INSTANT);
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(
                Optional.ofNullable(expectedFiling));

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                        .contentType(APPLICATION_JSON_MERGE_PATCH)
                        .requestAttr("transaction", transaction)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(FILING_ID)))
                .andExpect(jsonPath("$.ceased_on").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.identification.place_registered", is("place")))
                .andExpect(jsonPath("$.identification.legal_authority", is("authority")))
                .andExpect(jsonPath("$.identification.legal_form", is("form")))
                .andExpect(jsonPath("$.identification.registration_number", is("regNo")))
                .andExpect(jsonPath("$.identification.country_registered").doesNotExist())
                .andExpect(jsonPath("$.natures_of_control", is(empty())))
                .andExpect(jsonPath("$.links.self", is(SELF_URI.toString())))
                .andExpect(jsonPath("$.updated_at", is(SECOND_INSTANT.toString())));
    }

    @Test
    @DisplayName(
            "Expect updatedAt is updated even if there are no other changes in the PATCH request ")
    void updateFilingWhenFieldsAbsentThenTouchedButUnchanged() throws Exception {
        final var body = "{ }";
        final var filing = PscWithIdentificationFiling.builder()
                .id(FILING_ID)
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .name(CORPORATE_NAME)
                .identification(identification)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .updatedAt(FIRST_INSTANT)
                .build();
        final var expectedFiling = PscWithIdentificationFiling.builder(filing).updatedAt(
                SECOND_INSTANT).build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(pscFilingService.save(any(PscWithIdentificationFiling.class))).thenAnswer(
                i -> PscWithIdentificationFiling.builder(i.getArgument(0))
                        .build()); // copy of first argument
        when(clock.instant()).thenReturn(SECOND_INSTANT);
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(
                Optional.ofNullable(expectedFiling));

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                        .contentType(APPLICATION_JSON_MERGE_PATCH)
                        .requestAttr("transaction", transaction)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(FILING_ID)))
                .andExpect(jsonPath("$.reference_etag", is(ETAG)))
                .andExpect(jsonPath("$.reference_psc_id", is(PSC_ID)))
                .andExpect(jsonPath("$.ceased_on", is("2022-09-13")))
                .andExpect(jsonPath("$.name", is(CORPORATE_NAME)))
                .andExpect(jsonPath("$.identification.place_registered", is("place")))
                .andExpect(jsonPath("$.identification.legal_authority", is("authority")))
                .andExpect(jsonPath("$.identification.legal_form", is("form")))
                .andExpect(jsonPath("$.identification.registration_number", is("regNo")))
                .andExpect(jsonPath("$.identification.country_registered", is("country")))
                .andExpect(jsonPath("$.register_entry_date", is("2022-09-14")))
                .andExpect(jsonPath("$.updated_at", is(SECOND_INSTANT.toString())));
    }

    @Test
    @DisplayName("Expect PATCH validation to returns all errors when validation fails")
    void updateFilingWhenDatesInFuture() throws Exception {
        final var body = "{\n"
                + " \"ceased_on\": \"2023-11-05\", \n"
                + " \"register_entry_date\": \"2023-11-05\" \n"
                + "}";
        final var filing = PscWithIdentificationFiling.builder()
                .id(FILING_ID)
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .links(links)
                .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(clock.instant()).thenReturn(SECOND_INSTANT);

        final var expectedError = "must be a date in the past or in the present";
        final Map<String, String> expectedValues = Map.of("rejected", "2023-11-05");

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                        .contentType(APPLICATION_JSON_MERGE_PATCH)
                        .requestAttr("transaction", transaction)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].error",
                        containsInAnyOrder(expectedError, expectedError)))
                .andExpect(jsonPath("$.errors[*].error_values",
                        containsInAnyOrder(expectedValues, expectedValues)))
                .andExpect(jsonPath("$.errors[*].location_type",
                        containsInAnyOrder("json-path", "json-path")))
                .andExpect(jsonPath("$.errors[*].type",
                        containsInAnyOrder("ch:validation", "ch:validation")))
                .andExpect(jsonPath("$.errors[*].location",
                        containsInAnyOrder("$.ceased_on", "$.register_entry_date")));
    }

    @Test
    @DisplayName("Expect PATCH validation error to omit class names")
    void updateFilingWhenDateInvalid() throws Exception {
        final var body = "{\n"
                + " \"ceased_on\": \"2023-11-5\" \n"
                + "}";
        final var filing = PscWithIdentificationFiling.builder()
                .id(FILING_ID)
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .links(links)
                .build();
        final var expectedError = createExpectedValidationError(
                "JSON parse error: Text '2023-11-5' could not be parsed at index 8", "$.ceased_on",
                1, 14);

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(filing));
        when(clock.instant()).thenReturn(SECOND_INSTANT);

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                        .contentType(APPLICATION_JSON_MERGE_PATCH)
                        .requestAttr("transaction", transaction)
                        .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", not(containsString("java"))))
                .andExpect(jsonPath("$.errors[0]",
                        allOf(hasEntry("location", expectedError.getLocation()),
                                hasEntry("location_type", expectedError.getLocationType()),
                                hasEntry("type", expectedError.getType()))))
                .andExpect(jsonPath("$.errors[0].error", is("JSON parse error: Text '2023-11-5' could not be parsed at index 8")))
                .andExpect(jsonPath("$.errors[0].error_values",
                        allOf(hasEntry("offset", "line: 1, column: 14"), hasEntry("line", "1"),
                                hasEntry("column", "14"), hasEntry("rejected", "2023-11-5"))));
    }

    @Test
    @DisplayName("If the submission ID does not match then return a 404 Not Found response")
    void updateFilingWhenNotFoundThen404() throws Exception {
        final var body = "{ }";
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.empty());

        mockMvc.perform(patch(URL_PSC_CORPORATE_RESOURCE, TRANS_ID, FILING_ID).content(body)
                .contentType(APPLICATION_JSON_MERGE_PATCH)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders)).andDo(print()).andExpect(status().isNotFound());
    }

}