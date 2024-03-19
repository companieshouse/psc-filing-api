package uk.gov.companieshouse.pscfiling.api.mapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.IndividualFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.Identification;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@ExtendWith(SpringExtension.class)
@Import(FilingDataMapperImpl.class)
class FilingDataMapperTest {

    private LocalDate localDate1;
    private LocalDate localDate2;
    private Date3Tuple dob1;

    @Autowired
    private FilingDataMapper testMapper;

    @BeforeEach
    void setUp() {
        localDate1 = LocalDate.of(2019, 11, 5);
        localDate2 = LocalDate.of(2022, 6, 15);
        dob1 = new Date3Tuple(12, 9, 1970);
    }

    @Test
    void nameElementsApiToNameElements() {
        final var expectedNameElements = createNameElements();
        final var nameElementsApi = new NameElementsApi();
        nameElementsApi.setTitle("title");
        nameElementsApi.setForename("forename");
        nameElementsApi.setMiddleName("otherforenames");
        nameElementsApi.setSurname("surname");

        final var elements = testMapper.map(nameElementsApi);

        assertThat(elements, is(equalTo(expectedNameElements)));

    }

    @Test
    void nullNameElementsApiToNameElements() {

        final var elements = testMapper.map((NameElementsApi) null);

        assertThat(elements, is(nullValue()));

    }

    @Test
    void isoDateOfBirth() {
        final Date3Tuple tuple = new Date3Tuple(dob1.day(), dob1.month(), dob1.year());

        final var stringDob = testMapper.isoDateOfBirth(tuple);

        assertThat(stringDob, is(toIsoDate(tuple)));

    }

    @Test
    void isoDateOfBirthNull() {

        final var stringDob = testMapper.isoDateOfBirth(null);

        assertThat(stringDob, is(nullValue()));

    }

    @Test
    void mapDateOfBirth() {
        final var expected = new Date3Tuple(0, dob1.month(), dob1.year());
        final var dob = new DateOfBirth();
        dob.setMonth((long) dob1.month());
        dob.setYear((long) dob1.year());

        final var tuple = testMapper.map(dob);

        assertThat(tuple, is(equalTo(expected)));
    }

    @Test
    void mapNullDateOfBirth() {
        final var tuple = testMapper.map((DateOfBirth) null);

        assertThat(tuple, is(nullValue()));
    }

    @Test
    void mapEmptyDateOfBirth() {
        final var expected = new Date3Tuple(0, 0, 0);
        final var dob = new DateOfBirth();

        final var tuple = testMapper.map(dob);

        assertThat(tuple, is(equalTo(expected)));
    }


    @Test
    void filingToIndividualFilingDataDto() {
        final NameElements nameElements = createNameElements();
        final PscIndividualFiling filing = PscIndividualFiling.builder()
                .nameElements(nameElements)
                .dateOfBirth(new Date3Tuple(dob1.day(), dob1.month(), dob1.year()))
                .registerEntryDate(localDate1)
                .ceasedOn(localDate2)
                .build();

        final var filingDataDto = (IndividualFilingDataDto) testMapper.map(filing, PscTypeConstants.INDIVIDUAL);

        assertThat(filingDataDto.getTitle(), is(equalTo(filing.getNameElements().getTitle())));
        assertThat(filingDataDto.getFirstName(),
                is(equalTo(filing.getNameElements().getForename())));
        assertThat(filingDataDto.getOtherForenames(),
                is(equalTo(filing.getNameElements().getOtherForenames())));
        assertThat(filingDataDto.getLastName(),
                is(equalTo(filing.getNameElements().getSurname())));
        assertThat(filingDataDto.getDateOfBirth(),
                is(equalTo(toIsoDate(filing.getDateOfBirth()))));
        assertThat(filingDataDto.getRegisterEntryDate(),
                is(equalTo(toIsoDate(filing.getRegisterEntryDate()))));
        assertThat(filingDataDto.getCeasedOn(), is(equalTo(toIsoDate(filing.getCeasedOn()))));
    }

    @Test
    void nullFilingToIndividualFilingDataDto() {
        final var filing = testMapper.mapIndividual((PscIndividualFiling) null);

        assertThat(filing, is(nullValue()));
    }

    @Test
    void emptyFilingToIndividualFilingDataDto() {
        final PscCommunal emptyFiling = PscIndividualFiling.builder()
                .build();
        final var expectedDataDto = IndividualFilingDataDto.builder()
                .build();

        final var filingDataDto = testMapper.map(emptyFiling,PscTypeConstants.INDIVIDUAL );

        assertThat(filingDataDto, is(equalTo(expectedDataDto)));
    }

    @Test
    void filingEmptyNameElementsToIndividualFilingDataDto() {
        final PscCommunal emptyFiling = PscIndividualFiling.builder()
                .nameElements(NameElements.builder()
                        .build())
                .build();
        final var expectedDataDto = IndividualFilingDataDto.builder()
                .build();

        final var filingDataDto = testMapper.map(emptyFiling,PscTypeConstants.INDIVIDUAL );

        assertThat(filingDataDto, is(equalTo(expectedDataDto)));
    }

    @Test
    void identificationApiToIdentification() {
        final var expectedWithIdentification = createIdentification();

        final var identificationApi = new uk.gov.companieshouse.api.model.psc.Identification();
        identificationApi.setCountryRegistered("country");
        identificationApi.setPlaceRegistered("place");
        identificationApi.setRegistrationNumber("registration");
        identificationApi.setLegalAuthority("legalauthority");
        identificationApi.setLegalForm("legalform");

        final var identification = testMapper.map(identificationApi);

        assertThat(identification, is(equalTo(expectedWithIdentification)));
    }

    @Test
    void nullIdentificationApiToIdentification() {

        final var identification = testMapper.map((uk.gov.companieshouse.api.model.psc.Identification) null);

        assertThat(identification, is(nullValue()));
    }

    @Test
    void filingToCorporateIdentificationFilingDataDto() {

        final Identification identification = createIdentification();
        final PscWithIdentificationFiling filing = PscWithIdentificationFiling.builder()
                .identification(identification)
                .registerEntryDate(localDate1)
                .ceasedOn(localDate2)
                .build();

        final var filingDataDto = (WithIdentificationFilingDataDto) testMapper.map(filing,PscTypeConstants.CORPORATE_ENTITY );

        assertThat(filingDataDto.getCountryRegistered(),
                is(equalTo(filing.getIdentification().getCountryRegistered())));
        assertThat(filingDataDto.getLegalAuthority(),
                is(equalTo(filing.getIdentification().getLegalAuthority())));
        assertThat(filingDataDto.getLegalForm(),
                is(equalTo(filing.getIdentification().getLegalForm())));
        assertThat(filingDataDto.getPlaceRegistered(),
                is(equalTo(filing.getIdentification().getPlaceRegistered())));
        assertThat(filingDataDto.getRegistrationNumber(),
                is(equalTo(filing.getIdentification().getRegistrationNumber())));
        assertThat(filingDataDto.getRegisterEntryDate(),
                is(equalTo(toIsoDate(filing.getRegisterEntryDate()))));
        assertThat(filingDataDto.getCeasedOn(), is(equalTo(toIsoDate(filing.getCeasedOn()))));
    }

    @Test
    void filingToLegalPersonIdentificationFilingDataDto() {

        final Identification identification = createIdentification();
        final PscWithIdentificationFiling filing = PscWithIdentificationFiling.builder()
                .identification(identification)
                .registerEntryDate(localDate1)
                .ceasedOn(localDate2)
                .build();

        final var filingDataDto = (WithIdentificationFilingDataDto) testMapper.map(filing,PscTypeConstants.LEGAL_PERSON );

        assertThat(filingDataDto.getCountryRegistered(), is(nullValue()));
        assertThat(filingDataDto.getLegalAuthority(),
                is(equalTo(filing.getIdentification().getLegalAuthority())));
        assertThat(filingDataDto.getLegalForm(),
                is(equalTo(filing.getIdentification().getLegalForm())));
        assertThat(filingDataDto.getPlaceRegistered(), is(nullValue()));
        assertThat(filingDataDto.getRegistrationNumber(), is(nullValue()));
        assertThat(filingDataDto.getRegisterEntryDate(),
                is(equalTo(toIsoDate(filing.getRegisterEntryDate()))));
        assertThat(filingDataDto.getCeasedOn(), is(equalTo(toIsoDate(filing.getCeasedOn()))));
    }

    @Test
    void filingEmptyIdentificationToWithIdentificationFilingDataDto() {
        final PscCommunal emptyFiling = PscWithIdentificationFiling.builder()
                .identification(Identification.builder()
                        .build())
                .build();
        final var expectedDataDto = WithIdentificationFilingDataDto.builder()
                .build();

        final var filingDataDto = testMapper.map(emptyFiling,PscTypeConstants.CORPORATE_ENTITY );
        assertThat(filingDataDto, is(equalTo(expectedDataDto)));
    }

    @Test
    void nullFilingToWithIdentificationFilingDataDto() {
        final var filing = testMapper.map((PscWithIdentificationFiling) null, PscTypeConstants.CORPORATE_ENTITY);

        assertThat(filing, is(nullValue()));
    }

    @Test
    void nullFiling() {
        final var filing = testMapper.map((PscCommunal) null,PscTypeConstants.CORPORATE_ENTITY );

        assertThat(filing, is(nullValue()));
    }

    @Test
    void emptyFilingToWithIdentificationFilingDataDto() {
        final PscCommunal emptyFiling = PscWithIdentificationFiling.builder()
                .build();
        final var expectedDataDto = WithIdentificationFilingDataDto.builder()
                .build();

        final var filingDataDto = testMapper.map(emptyFiling,PscTypeConstants.CORPORATE_ENTITY );

        assertThat(filingDataDto, is(equalTo(expectedDataDto)));
    }

    @Test
    void enhanceIndividualFiling() {
        final PscCommunal filing = PscIndividualFiling.builder()
                .nameElements(createNameElements())
                .nationality("nation")
                .build();
        final PscApi pscDetails = new PscApi();
        pscDetails.setNationality("api nation");
        final var expectedNames = createNameElementsApi();
        pscDetails.setNameElements(expectedNames);

        final var enhanced =
                (PscIndividualFiling) testMapper.enhance(filing, PscTypeConstants.INDIVIDUAL,
                        pscDetails);

        assertThat(enhanced.getNameElements().getTitle(), is(expectedNames.getTitle()));
        assertThat(enhanced.getNameElements().getForename(),
                is(equalTo(expectedNames.getForename())));
        assertThat(enhanced.getNameElements().getOtherForenames(),
                is(expectedNames.getMiddleName()));
        assertThat(enhanced.getNameElements().getSurname(), is(expectedNames.getSurname()));
        assertThat(enhanced.getNationality(), is("nation"));
    }

    @Test
    void enhanceWithIdentificationFiling() {
        final PscCommunal filing = PscWithIdentificationFiling.builder()
                .identification(createIdentification())
                .name("name")
                .build();
        final PscApi pscDetails = new PscApi();
        pscDetails.setName("api name");

        final var expectedIdentification = createIdentificationApi();
        pscDetails.setIdentification(expectedIdentification);

        final var enhanced = (PscWithIdentificationFiling) testMapper.enhance(filing,
                PscTypeConstants.CORPORATE_ENTITY, pscDetails);

        assertThat(enhanced.getName(), is("api name"));
    }

    @Test
    void enhanceNullFiling() {
        final var enhanced =
                testMapper.enhance(null, PscTypeConstants.INDIVIDUAL, new PscApi());

        assertThat(enhanced, is(nullValue()));
    }

    @Test
    void enhanceNullDetails() {
        final PscCommunal filing =
                PscWithIdentificationFiling.builder().identification(createIdentification())
                        .build();

        final var enhanced = testMapper.enhance(filing, PscTypeConstants.CORPORATE_ENTITY, null);

        assertThat(enhanced, is(sameInstance(filing)));
    }

    private static String toIsoDate(final LocalDate date) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }

    private static String toIsoDate(final Date3Tuple tuple) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(
                LocalDate.of(tuple.year(), tuple.month(), tuple.day()));
    }

    private static NameElements createNameElements() {
        return NameElements.builder()
                .title("title")
                .forename("forename")
                .otherForenames("otherforenames")
                .surname("surname")
                .build();
    }

    private static NameElementsApi createNameElementsApi() {
        final NameElementsApi nameElementsApi = new NameElementsApi();

        nameElementsApi.setTitle("api title");
        nameElementsApi.setForename("api forename");
        nameElementsApi.setMiddleName("api middlename");
        nameElementsApi.setSurname("api surname");
        return nameElementsApi;
    }

    private static uk.gov.companieshouse.api.model.psc.Identification createIdentificationApi() {

        final uk.gov.companieshouse.api.model.psc.Identification identificationApi = new uk.gov.companieshouse.api.model.psc.Identification();

        identificationApi.setLegalForm("legal form");
        identificationApi.setRegistrationNumber("register entry");
        identificationApi.setPlaceRegistered("place registered");
        identificationApi.setCountryRegistered("country registered");
        identificationApi.setLegalAuthority("legal authority");

        return identificationApi;
    }

    private static Identification createIdentification() {
        return Identification.builder()
                .countryRegistered("country")
                .legalAuthority("legalauthority")
                .legalForm("legalform")
                .placeRegistered("place")
                .registrationNumber("registration")
                .build();
    }
}