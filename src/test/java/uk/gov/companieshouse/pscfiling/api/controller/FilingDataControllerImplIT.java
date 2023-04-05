package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.api.AttributeName;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.ApiEnumerationsConfig.PscFilingConfig;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("web")
@WebMvcTest(controllers = FilingDataControllerImpl.class)
@Import(PscFilingConfig.class)
class FilingDataControllerImplIT extends BaseControllerIT {
    @MockBean
    private FilingDataService filingDataService;
    @MockBean
    private PscFilingService pscFilingService;
    @MockBean
    private Logger logger;
    @MockBean
    private TransactionService transactionService;
    @Autowired
    private MockMvc mockMvc;

    private static final String URL_PSC =
            "/private/transactions/{id}/persons-with-significant-control";
    private static final String FILINGS_SUFFIX = "/{filingId" + "}/filings";

    public static Stream<Arguments> provideFilingData() {
        final Map<String, Object> nameElementsMap =
                Map.of("title", "title", "forename", "forename", "otherForenames",
                        "other forenames", "surname", "surname");
        final Map<String, Object> individualDataMap =
                Map.of("referenceEtag", ETAG, "referencePscId", PSC_ID, "filingResourceId",
                        CEASED_ON, "registerEntryDate", REGISTER_ENTRY, "nameElements",
                        nameElementsMap);

        final Map<String, Object> corporateIdentificationMap =
                Map.of("countryRegistered", "countryRegistered", "legalForm", "legalForm",
                        "legalAuthority", "legalAuthority", "registrationNumber",
                        "registrationNumber", "placeRegistered", "placeRegistered");
        final Map<String, Object> corporateDataMap =
                Map.of("name", "name", "referenceEtag", ETAG, "referencePscId", PSC_ID,
                        "filingResourceId", CEASED_ON, "registerEntryDate", REGISTER_ENTRY,
                        "identification", corporateIdentificationMap);

        final Map<String, Object> legalPersonIdentificationMap =
                Map.of("legalForm", "legalForm", "legalAuthority", "legalAuthority");
        final Map<String, Object> legalPersonDataMap =
                Map.of("name", "legal person name", "referenceEtag", ETAG, "referencePscId", PSC_ID,
                        "filingResourceId", CEASED_ON, "registerEntryDate", REGISTER_ENTRY,
                        "identification", legalPersonIdentificationMap);

        return Stream.of(Arguments.of(PscTypeConstants.INDIVIDUAL, individualDataMap),
                Arguments.of(PscTypeConstants.CORPORATE_ENTITY, corporateDataMap),
                Arguments.of(PscTypeConstants.LEGAL_PERSON, legalPersonDataMap));
    }

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
    }

    @ParameterizedTest
    @MethodSource("provideFilingData")
    void getFilingsWhenFound(final PscTypeConstants pscType, final Map<String, Object> dataMap)
            throws Exception {

        final var filingApi = new FilingApi();
        filingApi.setKind(FilingKind.PSC_CESSATION.getValue());
        filingApi.setData(dataMap);

        transaction.setStatus(TransactionStatus.CLOSED);
        when(filingDataService.generatePscFiling(FILING_ID, pscType, transaction,
                PASSTHROUGH_HEADER)).thenReturn(filingApi);

        mockMvc.perform(get(URL_PSC + "/" + pscType.getValue() + FILINGS_SUFFIX, TRANS_ID,
                        FILING_ID).headers(httpHeaders).requestAttr(AttributeName.TRANSACTION.getValue(), transaction)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].kind", is(FilingKind.PSC_CESSATION.getValue())));
    }

    @Test
    void getFilingsWhenNotFound() throws Exception {
        transaction.setStatus(TransactionStatus.CLOSED);

        when(filingDataService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                transaction, PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("for Not Found scenario", null));

        mockMvc.perform(get(URL_PSC + "/individual" + FILINGS_SUFFIX, TRANS_ID, FILING_ID).headers(
                        httpHeaders).requestAttr(AttributeName.TRANSACTION.getValue(), transaction))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Filing resource {filing-resource-id} not found")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("resource")));
    }
    @Test
    void getFilingsWhenNotFoundAndTransactionNull() throws Exception {

        mockMvc.perform(get(URL_PSC + "/individual" + FILINGS_SUFFIX, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @ParameterizedTest
    @EnumSource(value = TransactionStatus.class, names = {"CLOSED"}, mode = EnumSource.Mode.EXCLUDE)
    void getFilingsWhenFoundAndTransactionNotClosed(TransactionStatus transactionStatus)
            throws Exception {

        transaction.setStatus(transactionStatus);
        final var filingApi = new FilingApi();
        filingApi.setKind(FilingKind.PSC_CESSATION.getValue());

        when(filingDataService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                transaction, PASSTHROUGH_HEADER)).thenReturn(filingApi);

        mockMvc.perform(get(URL_PSC + "/" + PscTypeConstants.INDIVIDUAL.getValue() + FILINGS_SUFFIX,
                        TRANS_ID, FILING_ID).headers(httpHeaders).requestAttr(AttributeName.TRANSACTION.getValue(), transaction))
                .andExpect(status().isInternalServerError());
    }

}