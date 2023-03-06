package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.FilingDataConfig;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.FilingDataMapper;
import uk.gov.companieshouse.pscfiling.api.model.FilingKind;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.IndividualFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@ExtendWith(MockitoExtension.class)
class FilingDataServiceImplTest extends BaseServiceTestClass {

    private static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    private static final String TRANS_ID = "23445657412";
    private static final String REF_PSC_ID = "12345";
    private static final String REF_ETAG = "6789";
    private static final String CEASED_ON_STR = "2022-10-05";
    private static final LocalDate CEASED_ON = LocalDate.parse("2022-10-05");
    private static final String REGISTER_ENTRY_DATE = "2022-10-05";
    //private static final String PASSTHROUGH_HEADER = "passthrough";
    public static final String TITLE = "MR";
    public static final String FIRSTNAME = "JOE";
    public static final String OTHER_FORENAMES = "TOM";
    public static final String LASTNAME = "BLOGGS";
    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private FilingDataMapper dataMapper;
    @Mock
    private PscDetailsService pscDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private PscApi pscApi;
    @Mock
    private FilingDataConfig filingDataConfig;
    @Mock
    private Logger logger;
    private Transaction transaction;
    private FilingDataService testService;

    @BeforeEach
    void setUp() {
        testService = new FilingDataServiceImpl(pscFilingService, dataMapper, pscDetailsService,
                filingDataConfig, logger);
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");
    }

    @Test
    void generatePscIndividualFilingWhenFound() {
        final var filingData = IndividualFilingDataDto.builder()
                .title(TITLE)
                .firstName(FIRSTNAME)
                .otherForenames(OTHER_FORENAMES)
                .lastName(LASTNAME)
                .ceasedOn(CEASED_ON_STR)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();

        final var nameElements =
                NameElements.builder().title(TITLE).forename(FIRSTNAME).surname(LASTNAME)
                        .build();
        final PscCommunal pscFiling = PscIndividualFiling.builder()
                .referencePscId(REF_PSC_ID)
                .referenceEtag(REF_ETAG)
                .ceasedOn(CEASED_ON)
                .build();
        final PscCommunal enhancedPscFiling =
                PscIndividualFiling.builder((PscIndividualFiling) pscFiling)
                        .nameElements(nameElements)
                        .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(pscFiling));
        when(pscDetailsService.getPscDetails(transaction, REF_PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscApi);
        final var nameElementsApi = new NameElementsApi();
        nameElementsApi.setTitle(TITLE);
        nameElementsApi.setForename(FIRSTNAME);
        nameElementsApi.setSurname(LASTNAME);
        when(dataMapper.enhance(pscFiling, pscApi)).thenReturn(enhancedPscFiling);
        when(dataMapper.map(enhancedPscFiling)).thenReturn(filingData);

        final var filingApi =
                testService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL, transaction,
                        PASSTHROUGH_HEADER);

        final Map<String, Object> expectedMap =
                Map.of("title", TITLE, "first_name", FIRSTNAME, "other_forenames", OTHER_FORENAMES,
                        "last_name", LASTNAME, "ceased_on", CEASED_ON_STR, "register_entry_date",
                        REGISTER_ENTRY_DATE);

        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(), is(FilingKind.PSC_CESSATION.getValue()));
    }

    @Test
    void generatePscIndividualFilingWhenNotFound() {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        final var exception = assertThrows(FilingResourceNotFoundException.class,
                () -> testService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                        transaction, PASSTHROUGH_HEADER));

        assertThat(exception.getMessage(),
                is("Psc individual not found when generating filing for " + FILING_ID));
    }
}