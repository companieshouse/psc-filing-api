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
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.dto.FilingDataDto;

@ExtendWith(MockitoExtension.class)
class FilingDataServiceImplTest {

    private static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    private static final String TRANS_ID = "23445657412";
    private static final String REF_PSC_ID = "12345";
    private static final String REF_ETAG = "6789";
    private static final String CEASED_ON_STR = "2022-10-05";
    private static final LocalDate CEASED_ON = LocalDate.parse("2022-10-05");
    private static final String REGISTER_ENTRY_DATE = "2022-10-05";
    private static final String PASSTHROUGH_HEADER = "passthrough";
    public static final String FIRSTNAME = "JOE";
    public static final String OTHER_FORENAMES = "TOM";
    public static final String LASTNAME = "BLOGGS";
    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private PscIndividualMapper pscIndividualMapper;
    @Mock
    private PscDetailsService pscDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private PscApi pscApi;
    @Mock
    private Logger logger;
    private Transaction transaction;
    private FilingDataService testService;

    @BeforeEach
    void setUp() {
        testService = new FilingDataServiceImpl(pscFilingService, pscIndividualMapper,
            pscDetailsService, logger);
        transaction = new Transaction();
        transaction.setId(TRANS_ID);
        transaction.setCompanyNumber("012345678");
    }

    @Test
    void generatePscIndividualFilingWhenFound() {
        final var filingData = FilingDataDto.builder()
            .firstName(FIRSTNAME)
            .otherForenames(OTHER_FORENAMES)
            .lastName(LASTNAME)
            .ceasedOn(CEASED_ON_STR)
            .registerEntryDate(REGISTER_ENTRY_DATE).build();

        final var nameElements = NameElements.builder().forename(FIRSTNAME).surname(LASTNAME).build();
        final var pscFiling = PscIndividualFiling.builder()
                .referencePscId(REF_PSC_ID)
                .referenceEtag(REF_ETAG)
                .nameElements(nameElements)
                .ceasedOn(CEASED_ON)
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(pscFiling));
        when(pscIndividualMapper.mapFiling(pscFiling)).thenReturn(filingData);
        when(pscDetailsService.getPscDetails(transaction, REF_PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscApi);
        var nameElementsApi = new NameElementsApi();
        nameElementsApi.setForename(FIRSTNAME);
        nameElementsApi.setSurname(LASTNAME);
        when(pscApi.getNameElements()).thenReturn(nameElementsApi);
        when(pscIndividualMapper.mapFiling(pscFiling)).thenReturn(filingData);

        final var filingApi = testService.generatePscFiling(FILING_ID, transaction, PASSTHROUGH_HEADER);

        final Map<String, Object> expectedMap =
                Map.of("first_name", FIRSTNAME,
                        "other_forenames", OTHER_FORENAMES,
                        "last_name", LASTNAME,
                        "ceased_on", CEASED_ON_STR,
                        "register_entry_date",REGISTER_ENTRY_DATE );

        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(), is("psc-filing#cessation"));
    }

    @Test
    void generatePscIndividualFilingWhenNotFound() {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        final var exception = assertThrows(FilingResourceNotFoundException.class,
                () -> testService.generatePscFiling(FILING_ID, transaction, PASSTHROUGH_HEADER));

        assertThat(exception.getMessage(),
                is("Psc individual not found when generating filing for " + FILING_ID));
    }
}