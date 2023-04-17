package uk.gov.companieshouse.pscfiling.api.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Identification;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingDataService;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

@ExtendWith(MockitoExtension.class)
class FilingDataServiceImplTest extends TestBaseService {
    private static final String REF_PSC_ID = "12345";
    private static final String REF_ETAG = "6789";
    private static final String CEASED_ON_STR = "2022-10-05";
    private static final LocalDate CEASED_ON = LocalDate.parse("2022-10-05");
    private static final String REGISTER_ENTRY_DATE = "2022-10-05";
    private static final String TITLE = "MR";
    private static final String FIRSTNAME = "JOE";
    private static final String OTHER_FORENAMES = "TOM";
    private static final String LASTNAME = "BLOGGS";
    private static final String CORPORATE_NAME = "corporate name";
    private static final String COUNTRY_REGISTERED = "country registered";
    private static final String LEGAL_FORM = "legal form";
    private static final String LEGAL_AUTHORITY = "legal authority";
    private static final String REGISTRATION_NUMBER = "reg no";
    private static final String PLACE_REGISTERED = "place registered";
    private static final String LEGAL_NAME = "legal name";
    protected static final String INDIVIDUAL = "individual";
    protected static final String CORPORATE_ENTITY = "corporate-entity";
    protected static final String LEGAL_PERSON = "legal-person";


    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private FilingDataMapper dataMapper;
    @Mock
    private PscDetailsService pscDetailsService;
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
        transaction.setCompanyNumber(COMPANY_NUMBER);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {OTHER_FORENAMES})
    void generatePscIndividualFilingWhenFound(final String otherForenames) {
        final var filingData = IndividualFilingDataDto.builder()
                .title(TITLE)
                .firstName(FIRSTNAME)
                .otherForenames(otherForenames)
                .lastName(LASTNAME)
                .ceasedOn(CEASED_ON_STR)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();

        final var nameElements =
                NameElements.builder().title(TITLE).forename(FIRSTNAME).surname(LASTNAME)
                        .build();
        final var pscFiling = PscIndividualFiling.builder()
                .referencePscId(REF_PSC_ID)
                .referenceEtag(REF_ETAG)
                .ceasedOn(CEASED_ON)
                .build();
        final var enhancedPscFiling =
                PscIndividualFiling.builder(pscFiling).nameElements(nameElements)
                        .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(pscFiling));
        when(pscDetailsService.getPscDetails(transaction, REF_PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscApi);
        final var nameElementsApi = new NameElementsApi();
        nameElementsApi.setTitle(TITLE);
        nameElementsApi.setForename(FIRSTNAME);
        nameElementsApi.setSurname(LASTNAME);
        when(dataMapper.enhance(pscFiling, PscTypeConstants.INDIVIDUAL, pscApi)).thenReturn(
                enhancedPscFiling);
        when(dataMapper.map(enhancedPscFiling, PscTypeConstants.INDIVIDUAL)).thenReturn(filingData);
        when(filingDataConfig.getPsc07Description()).thenReturn(
                "(PSC07) Notice of ceasing to be a Person of Significant Control for {0} on {1}");

        final var filingApi =
                testService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL, transaction,
                        PASSTHROUGH_HEADER);

        final Map<String, Object> expectedMap;
        final String expectedDescription;
        if (otherForenames == null) {
            expectedMap = Map.of("title", TITLE, "first_name", FIRSTNAME, "last_name",
                            LASTNAME, "ceased_on", CEASED_ON_STR, "register_entry_date", REGISTER_ENTRY_DATE);
            expectedDescription =
                    "(PSC07) Notice of ceasing to be a Person of Significant Control for " + TITLE + " " +
                            FIRSTNAME + " " + LASTNAME + " on 5 October 2022";
        }
        else {
            expectedMap = Map.of("title", TITLE, "first_name", FIRSTNAME, "other_forenames", otherForenames, "last_name",
                            LASTNAME, "ceased_on", CEASED_ON_STR, "register_entry_date", REGISTER_ENTRY_DATE);
            expectedDescription =
                    "(PSC07) Notice of ceasing to be a Person of Significant Control for " + TITLE + " " +
                            FIRSTNAME + " " + OTHER_FORENAMES + " " + LASTNAME + " on 5 October 2022";
        }
        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(),
                is(MessageFormat.format("{0}#{1}", FilingKind.PSC_CESSATION.getValue(), INDIVIDUAL)));
        assertThat(filingApi.getDescription(), is(expectedDescription));
    }

    @Test
    void generatePscCorporateEntityFilingWhenFound() {
        final var filingData = WithIdentificationFilingDataDto.builder()
                .name(CORPORATE_NAME)
                .countryRegistered(COUNTRY_REGISTERED)
                .legalForm(LEGAL_FORM)
                .legalAuthority(LEGAL_AUTHORITY)
                .registrationNumber(REGISTRATION_NUMBER)
                .placeRegistered(PLACE_REGISTERED)
                .ceasedOn(CEASED_ON_STR)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();

        final var identification = Identification.builder()
                .countryRegistered(COUNTRY_REGISTERED)
                .placeRegistered(PLACE_REGISTERED)
                .legalAuthority(LEGAL_AUTHORITY)
                .legalForm(LEGAL_FORM)
                .registrationNumber(REGISTRATION_NUMBER)
                .build();

        final var pscFiling = PscWithIdentificationFiling.builder()
                .referencePscId(REF_PSC_ID)
                .referenceEtag(REF_ETAG)
                .ceasedOn(CEASED_ON)
                .build();

        final var enhancedPscFiling =
                PscWithIdentificationFiling.builder(pscFiling)
                        .name("psc_name")
                        .identification(identification)
                        .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(pscFiling));
        when(pscDetailsService.getPscDetails(transaction, REF_PSC_ID,
                PscTypeConstants.CORPORATE_ENTITY, PASSTHROUGH_HEADER)).thenReturn(pscApi);

        pscApi.setName(CORPORATE_NAME);
        when(dataMapper.enhance(pscFiling, PscTypeConstants.CORPORATE_ENTITY, pscApi)).thenReturn(
                enhancedPscFiling);
        when(dataMapper.map(enhancedPscFiling, PscTypeConstants.CORPORATE_ENTITY)).thenReturn(
                filingData);
        when(filingDataConfig.getPsc07Description()).thenReturn(
                "(PSC07) Notice of ceasing to be a Person of Significant Control for {0} on {1}");

        final var filingApi =
                testService.generatePscFiling(FILING_ID, PscTypeConstants.CORPORATE_ENTITY,
                        transaction, PASSTHROUGH_HEADER);

        final Map<String, Object> expectedMap =
                Map.of("country_registered", COUNTRY_REGISTERED, "place_registered",
                        PLACE_REGISTERED, "registration_number", REGISTRATION_NUMBER,
                        "legal_authority", LEGAL_AUTHORITY, "legal_form", LEGAL_FORM, "ceased_on",
                        CEASED_ON_STR, "name", CORPORATE_NAME, "register_entry_date",
                        REGISTER_ENTRY_DATE);
        final String expectedDescription =
                "(PSC07) Notice of ceasing to be a Person of Significant Control for " +
                        CORPORATE_NAME + " on 5 October 2022";

        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(),
                is(MessageFormat.format("{0}#{1}", FilingKind.PSC_CESSATION.getValue(), CORPORATE_ENTITY)));
        assertThat(filingApi.getDescription(), is(expectedDescription));
    }


    @Test
    void generatePscLegalPersonFilingWhenFound() {
        final var filingData = WithIdentificationFilingDataDto.builder()
                .name(LEGAL_NAME)
                .legalForm(LEGAL_FORM)
                .legalAuthority(LEGAL_AUTHORITY)
                .ceasedOn(CEASED_ON_STR)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .build();

        final var identification = Identification.builder()
                .countryRegistered(COUNTRY_REGISTERED)
                .placeRegistered(PLACE_REGISTERED)
                .legalAuthority(LEGAL_AUTHORITY)
                .legalForm(LEGAL_FORM)
                .registrationNumber(REGISTRATION_NUMBER)
                .build();

        final var pscFiling = PscWithIdentificationFiling.builder()
                .referencePscId(REF_PSC_ID)
                .referenceEtag(REF_ETAG)
                .ceasedOn(CEASED_ON)
                .build();

        final var enhancedPscFiling =
                PscWithIdentificationFiling.builder(pscFiling)
                        .name("psc_name")
                        .identification(identification)
                        .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(pscFiling));
        when(pscDetailsService.getPscDetails(transaction, REF_PSC_ID, PscTypeConstants.LEGAL_PERSON,
                PASSTHROUGH_HEADER)).thenReturn(pscApi);

        pscApi.setName(LEGAL_NAME);
        when(dataMapper.enhance(pscFiling, PscTypeConstants.LEGAL_PERSON, pscApi)).thenReturn(
                enhancedPscFiling);
        when(dataMapper.map(enhancedPscFiling, PscTypeConstants.LEGAL_PERSON)).thenReturn(
                filingData);
        when(filingDataConfig.getPsc07Description()).thenReturn(
                "(PSC07) Notice of ceasing to be a Person of Significant Control for {0} on {1}");

        final var filingApi =
                testService.generatePscFiling(FILING_ID, PscTypeConstants.LEGAL_PERSON, transaction,
                        PASSTHROUGH_HEADER);

        final Map<String, Object> expectedMap =
                Map.of("legal_authority", LEGAL_AUTHORITY, "legal_form", LEGAL_FORM, "ceased_on",
                        CEASED_ON_STR, "name", LEGAL_NAME, "register_entry_date",
                        REGISTER_ENTRY_DATE);
        final String expectedDescription =
                "(PSC07) Notice of ceasing to be a Person of Significant Control for " +
                        LEGAL_NAME + " on 5 October 2022";

        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(),
                is(MessageFormat.format("{0}#{1}", FilingKind.PSC_CESSATION.getValue(), LEGAL_PERSON)));
        assertThat(filingApi.getDescription(), is(expectedDescription));
    }

    @Test
    void generatePscLegalPersonFilingWhenHasCorporateIdentityData() {
        //DTO generated from the CHIPS and psc filing MongoDB data
        final var filingDataDto = WithIdentificationFilingDataDto.builder()
                .name(LEGAL_NAME)
                .registerEntryDate(REGISTER_ENTRY_DATE)
                .ceasedOn(CEASED_ON_STR)
                .legalForm(LEGAL_FORM)
                .legalAuthority(LEGAL_AUTHORITY)
                .build();

        //from sdk api
        final var identificationApi = new uk.gov.companieshouse.api.model.psc.Identification();
        identificationApi.setLegalAuthority(LEGAL_AUTHORITY);
        identificationApi.setLegalForm(LEGAL_FORM);

        final var pscDetails = new PscApi();
        pscDetails.setName(LEGAL_NAME);
        pscDetails.setIdentification(identificationApi);

        //psc filing entity retrieved from psc filing api MongoDB
        //Note contains fields that are not necessary for a legal person
        final var identification = Identification.builder()
                .legalAuthority(LEGAL_AUTHORITY)
                .legalForm(LEGAL_FORM)
                .countryRegistered(COUNTRY_REGISTERED)
                .registrationNumber(REGISTRATION_NUMBER)
                .placeRegistered(PLACE_REGISTERED)
                .build();

        final var pscFiling = PscWithIdentificationFiling.builder()
                .name("entity name")
                .referencePscId(REF_PSC_ID)
                .referenceEtag(REF_ETAG)
                .ceasedOn(CEASED_ON)
                .identification(identification)
                .build();

        //adding the data from the sdk api to our data (i.e. lookup from CHIPS)
        final PscCommunal enhancedPscFiling = PscWithIdentificationFiling.builder(pscFiling)
                .name(LEGAL_NAME)
                .identification(identification)
                .build();

        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.of(pscFiling));
        when(dataMapper.enhance(pscFiling, PscTypeConstants.LEGAL_PERSON, pscDetails)).thenReturn(
                enhancedPscFiling);
        when(pscDetailsService.getPscDetails(transaction, REF_PSC_ID, PscTypeConstants.LEGAL_PERSON,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(dataMapper.map(enhancedPscFiling, PscTypeConstants.LEGAL_PERSON)).thenReturn(
                filingDataDto);
        when(filingDataConfig.getPsc07Description()).thenReturn(
                "(PSC07) Notice of ceasing to be a Person of Significant Control for {0} on {1}");

        final var filingApi =
                testService.generatePscFiling(FILING_ID, PscTypeConstants.LEGAL_PERSON, transaction,
                        PASSTHROUGH_HEADER);

        final Map<String, Object> expectedMap =
                Map.of("ceased_on", CEASED_ON_STR, "name", LEGAL_NAME, "register_entry_date",
                        REGISTER_ENTRY_DATE, "legal_authority", LEGAL_AUTHORITY, "legal_form",
                        LEGAL_FORM);

        assertThat(filingApi.getData(), is(equalTo(expectedMap)));
        assertThat(filingApi.getKind(),
                is(MessageFormat.format("{0}#{1}", FilingKind.PSC_CESSATION.getValue(), LEGAL_PERSON)));
    }

    @Test
    void generatePscIndividualFilingWhenNotFound() {
        when(pscFilingService.get(FILING_ID)).thenReturn(Optional.empty());

        final var exception = assertThrows(FilingResourceNotFoundException.class,
                () -> testService.generatePscFiling(FILING_ID, PscTypeConstants.INDIVIDUAL,
                        transaction, PASSTHROUGH_HEADER));

        assertThat(exception.getMessage(),
                is("PSC filing not found when generating filing for " + FILING_ID));
    }
}