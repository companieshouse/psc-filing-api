package uk.gov.companieshouse.pscfiling.api.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.ResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.mapper.PscIndividualMapper;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.filing.FilingData;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingDataServiceImplTest {

    private static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    private static final String TRANS_ID = "23445657412";
    private static final String REF_APPOINTMENT_ID = "12345";
    private static final String REF_ETAG = "6789";

    private static final String CEASED_ON_STR = "2022-10-05";
    private static final LocalDate CEASED_ON = LocalDate.parse("2022-10-05");
    public static final String FIRSTNAME = "JOE";
    public static final String LASTNAME = "BLOGGS";
    public static final String DATE_OF_BIRTH_STR = "2000-10-20";
    public static final Date3Tuple DATE_OF_BIRTH_TUPLE = new Date3Tuple(20, 10, 2000);
    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private PscIndividualMapper pscIndividualMapper;
    @Mock
    private Logger logger;
    private FilingDataService testService;

    @BeforeEach
    void setUp() {
        testService = new FilingDataServiceImpl(pscFilingService, pscIndividualMapper, logger);
    }

    @Test
    void generatePscIndividualFilingWhenFound() {
        final var filingData = new FilingData(FIRSTNAME, LASTNAME, DATE_OF_BIRTH_STR, CEASED_ON_STR);
        final var nameElements = NameElements.builder().forename(FIRSTNAME).surname(LASTNAME).build();
        final var pscFiling = PscIndividualFiling.builder()
                .referencePscId(REF_APPOINTMENT_ID)
                .referenceEtag(REF_ETAG)
                .nameElements(nameElements)
                .ceasedOn(CEASED_ON)
                .dateOfBirth(DATE_OF_BIRTH_TUPLE)
                .build();

        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(pscFiling));
        when(pscIndividualMapper.mapFiling(pscFiling)).thenReturn(filingData);

        final var filingApi = testService.generatePscFiling(TRANS_ID, FILING_ID);

        final Map<String, Object> expectedMap =
                Map.of("first_name", FIRSTNAME, "last_name", LASTNAME,
                        "date_of_birth", DATE_OF_BIRTH_STR,
                        "resigned_on", CEASED_ON_STR);

        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(), is("psc-filing#ceasation"));
    }

    @Test
    void generatePscIndividualFilingWhenNotFound() {
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        final var exception = assertThrows(ResourceNotFoundException.class,
                () -> testService.generatePscFiling(TRANS_ID, FILING_ID));

        assertThat(exception.getMessage(),
                is("Psc individual not found when generating filing for " + FILING_ID));
    }
}