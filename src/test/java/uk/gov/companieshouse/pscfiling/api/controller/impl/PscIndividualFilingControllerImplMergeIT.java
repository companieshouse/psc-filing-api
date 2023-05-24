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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.time.Clock;
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
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.NaturesOfControlList;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationService;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("app")
@SpringBootTest
@AutoConfigureMockMvc
class PscIndividualFilingControllerImplMergeIT extends BaseControllerIT {
    private static final String RESOURCE_URI_STR = String.format(
        "/transactions/%s/persons-with-significant-control/individual/%s", TRANS_ID, FILING_ID);
    private static final URI SELF_URI = URI.create(RESOURCE_URI_STR);
    private static final URI VALIDATION_URI = URI.create(RESOURCE_URI_STR + "/validation_status");
    private NameElements nameElements;
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
    private PscFilingRepository filingRepository;
    @MockBean
    private PscIndividualFilingRepository individualFilingRepository;
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
        nameElements = NameElements.builder()
                .forename("Forename")
                .otherForenames("Other Forenames")
                .surname("Surname")
                .title("Sir")
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
            + "  \"ceased_on\": \"2022-03-03\",\n"
            + " \"created_at\": \""
            + SECOND_INSTANT
            + "\",\n"
            + " \"updated_at\": \""
            + FIRST_INSTANT
            + "\",\n"
            + "  \"name_elements\": {\n"
            + "    \"surname\": \"Replaced\"\n"
            + "  },\n"
            + " \"natures_of_control\": [\n"
            + "    \"type4\"\n"
            + "  ]\n"
            + "}";
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .createdAt(FIRST_INSTANT)
            .updatedAt(FIRST_INSTANT)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .ceasedOn(CEASED_ON_DATE)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .nameElements(nameElements)
            .naturesOfControl(naturesOfControl)
            .links(links)
            .build();

        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.save(any(PscIndividualFiling.class))).thenAnswer(
            i -> PscIndividualFiling.builder(i.getArgument(0))
                .build()); // copy of first argument
        when(clock.instant()).thenReturn(SECOND_INSTANT);

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
                .contentType(APPLICATION_JSON_MERGE_PATCH)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(FILING_ID)))
            .andExpect(jsonPath("$.ceased_on", is("2022-03-03")))
                .andExpect(jsonPath("$.name_elements.forename", is("Forename")))
                .andExpect(jsonPath("$.name_elements.other_forenames", is("Other Forenames")))
                .andExpect(jsonPath("$.name_elements.title", is("Sir")))
                .andExpect(jsonPath("$.name_elements.surname", is("Replaced")))
                .andExpect(jsonPath("$.natures_of_control", containsInAnyOrder("type4")))
                .andExpect(jsonPath("$.updated_at", is(SECOND_INSTANT.toString())))
                .andExpect(jsonPath("$.created_at", is(FIRST_INSTANT.toString())))
                .andExpect(header().stringValues("Location", links.getSelf().toString()));
    }

    @Test
    @DisplayName("Expect to add new fields to existing filing")
    void updateFilingWhenAddingFields() throws Exception {
        final var body = "{\n"
            + "  \"ceased_on\": \"2022-10-05\",\n"
            + "  \"name_elements\": {\n"
            + "    \"surname\": \"Added\"\n"
            + "  },\n"
            + " \"natures_of_control\": [\n"
            + "    \"type1\",\n"
            + "    \"type2\",\n"
            + "    \"type3\",\n"
            + "    \"type4\"\n"
            + "  ]\n"
            + "}";
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .links(links)
            .build();

        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.save(any(PscIndividualFiling.class))).thenAnswer(
            i -> PscIndividualFiling.builder(i.getArgument(0))
                .build()); // copy of first argument
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
                .contentType(APPLICATION_JSON_MERGE_PATCH)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(FILING_ID)))
            .andExpect(jsonPath("$.ceased_on", is(CEASED_ON)))
                .andExpect(jsonPath("$.name_elements.forename").doesNotExist())
                .andExpect(jsonPath("$.name_elements.other_forenames").doesNotExist())
                .andExpect(jsonPath("$.name_elements.title").doesNotExist())
                .andExpect(jsonPath("$.name_elements.surname", is("Added")))
                .andExpect(jsonPath("$.natures_of_control",
                        containsInAnyOrder("type1", "type2", "type3", "type4")))
                .andExpect(jsonPath("$.updated_at", is(FIRST_INSTANT.toString())))
                .andExpect(header().stringValues("Location", links.getSelf().toString()));
    }

    @Test
    @DisplayName("Expected: top level and nested fields are deleted with 'null' and read only fields are unchanged")
    void updateFilingWhenDeletingFields() throws Exception {
        final var body = "{\n"
            + " \"id\": null,\n"
            + "  \"ceased_on\": null,\n"
            + "  \"name_elements\": {\n"
            + "    \"surname\": null\n"
            + "  },\n"
            + " \"links\": {\n"
            + "    \"self\": null\n"
            + "  },\n"
            + " \"natures_of_control\": [\n"
            + "  ]\n"
            + "}";
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .ceasedOn(CEASED_ON_DATE)
            .nameElements(nameElements)
            .links(links)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .build();

        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.save(any(PscIndividualFiling.class))).thenAnswer(
            i -> PscIndividualFiling.builder(i.getArgument(0))
                .build()); // copy of first argument
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
                .contentType(APPLICATION_JSON_MERGE_PATCH)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(FILING_ID)))
            .andExpect(jsonPath("$.ceased_on").doesNotExist())
                .andExpect(jsonPath("$.name_elements.forename", is("Forename")))
                .andExpect(jsonPath("$.name_elements.other_forenames", is("Other Forenames")))
                .andExpect(jsonPath("$.name_elements.title", is("Sir")))
                .andExpect(jsonPath("$.name_elements.surname").doesNotExist())
                .andExpect(jsonPath("$.natures_of_control", is(empty())))
                .andExpect(jsonPath("$.links.self", is(SELF_URI.toString())))
                .andExpect(jsonPath("$.updated_at", is(FIRST_INSTANT.toString())))
                .andExpect(header().stringValues("Location", links.getSelf().toString()));
    }

    @Test
    @DisplayName("Expect updatedAt is updated even if there are no other changes in the PATCH request ")
    void updateFilingWhenFieldsAbsentThenTouchedButUnchanged() throws Exception {
        final var body = "{ }";
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .ceasedOn(CEASED_ON_DATE)
            .nameElements(nameElements)
            .links(links)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .updatedAt(FIRST_INSTANT)
            .build();

        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.save(any(PscIndividualFiling.class))).thenAnswer(
            i -> PscIndividualFiling.builder(i.getArgument(0))
                .build()); // copy of first argument
        when(clock.instant()).thenReturn(SECOND_INSTANT);

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
                .contentType(APPLICATION_JSON_MERGE_PATCH)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(FILING_ID)))
            .andExpect(jsonPath("$.reference_etag", is(ETAG)))
                .andExpect(jsonPath("$.reference_psc_id", is(PSC_ID)))
                .andExpect(jsonPath("$.ceased_on", is("2022-09-13")))
                .andExpect(jsonPath("$.name_elements.forename", is("Forename")))
                .andExpect(jsonPath("$.name_elements.other_forenames", is("Other Forenames")))
                .andExpect(jsonPath("$.name_elements.title", is("Sir")))
                .andExpect(jsonPath("$.name_elements.surname", is("Surname")))
                .andExpect(jsonPath("$.register_entry_date", is("2022-09-14")))
                .andExpect(jsonPath("$.updated_at", is(SECOND_INSTANT.toString())))
                .andExpect(header().stringValues("Location", links.getSelf().toString()));
    }

    @Test
    @DisplayName("Expect PATCH validation to return an error when validation fails")
    void updateFilingWhenDateInFuture() throws Exception {
        final var body = "{\n"
            + " \"ceased_on\": \"2023-11-05\", \n"
            + " \"register_entry_date\": \"2023-11-05\" \n"
            + "}";
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .links(links)
            .build();

        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        final var expectedError = "{rejected-value} must be a date in the past or in the present";
        final Map<String, String> expectedValues = Map.of("rejected-value", "2023-11-05");

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
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
                        containsInAnyOrder("$.ceased_on", "$.register_entry_date")))
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    @DisplayName("Expect PATCH validation error to omit class names")
    void updateFilingWhenDateInvalid() throws Exception {
        final var body = "{\n"
            + " \"ceased_on\": \"2023-11-5\" \n"
            + "}";
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .links(links)
            .build();
        final var expectedError = createExpectedValidationError(
            "JSON parse error: Text '2023-11-5' could not be parsed at index 8", "$.ceased_on",
            1, 14);

        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
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
            .andExpect(jsonPath("$.errors[0].error",
                is("Failed to merge patch request: Text '2023-11-5' could not be parsed " +
                    "at index 8")))
            .andExpect(jsonPath("$.errors[0].error_values",
                allOf(hasEntry("offset", "line: 1, column: 14"), hasEntry("line", "1"),
                    hasEntry("column", "14"), hasEntry("rejected-value", "2023-11-5"))))
            .andExpect(header().doesNotExist("Location"));
    }

    @Test
    @DisplayName("If the submission ID does not match then return a 404 Not Found response")
    void updateFilingWhenNotFoundThen404() throws Exception {
        final var body = "{ }";
        when(individualFilingRepository.findById(FILING_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
            patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body).contentType(
                APPLICATION_JSON_MERGE_PATCH).requestAttr("transaction", transaction).headers(
                httpHeaders)).andDo(print()).andExpect(status().isNotFound()).andExpect(
            header().doesNotExist("Location")).andExpect(
            jsonPath("$.errors", hasSize(1))).andExpect(jsonPath("$.errors[0].error",
            is("Filing resource {filing-resource-id} not found"))).andExpect(
            jsonPath("$.errors[0].type", is("ch:validation"))).andExpect(
            jsonPath("$.errors[0].location_type", is("resource")));
    }

    @Test
    @DisplayName("If the request URI does not match the filing's 'self' link then return a 404 " +
        "Not Found response")
    void updateFilingWhenTransactionIdMismatchThen404() throws Exception {
        final var body = "{ }";
        final URI BAD_SELF_URI = URI.create("/path/to/other_or_bad");
        final var links = new Links(BAD_SELF_URI, VALIDATION_URI);
        final var filing = PscIndividualFiling.builder()
            .id(FILING_ID)
            .referenceEtag(ETAG)
            .referencePscId(PSC_ID)
            .registerEntryDate(REGISTER_ENTRY_DATE)
            .links(links)
            .build();

        mockMvc.perform(patch(URL_PSC_INDIVIDUAL_RESOURCE, TRANS_ID, FILING_ID).content(body)
                .contentType(APPLICATION_JSON_MERGE_PATCH)
                .requestAttr("transaction", transaction)
                .headers(httpHeaders))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(header().doesNotExist("Location"))
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(
                jsonPath("$.errors[0].error", is("Filing resource {filing-resource-id} not found")))
            .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
            .andExpect(jsonPath("$.errors[0].location_type", is("resource")));
    }

}