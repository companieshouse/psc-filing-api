package uk.gov.companieshouse.pscfiling.api.model.dto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PscIndividualDtoTest {

    private PscIndividualDto testDto;
    private AddressDto addressDto;
    private LocalDate ceasedOn;
    private LocalDate notifiedOn;
    private LocalDate registerEntryDate;
    private NameElementsDto nameElementsDto;

    @BeforeEach
    void setUp() {
        addressDto = AddressDto.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .careOf("careOf")
                .country("country")
                .locality("locality")
                .poBox("poBox")
                .postalCode("postalCode")
                .premises("premises")
                .region("region")
                .build();
        ceasedOn = LocalDate.of(2022,11,21);
        Date3TupleDto dob1 = new Date3TupleDto(12, 9, 1970);
        nameElementsDto = NameElementsDto.builder()
                .forename("forename")
                .otherForenames("other")
                .surname("surname")
                .title("title")
                .build();
        notifiedOn = LocalDate.of(2022,11,10);
        registerEntryDate = LocalDate.of(2022,11,5);
        PscIndividualDto.Builder testBuilder = PscIndividualDto.builder();
        testDto = testBuilder.address(addressDto)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(ceasedOn)
                .countryOfResidence("Wales")
                .dateOfBirth(dob1)
                .nameElements(nameElementsDto)
                .nationality("nationality")
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(notifiedOn)
                .referenceEtag("etag")
                .referencePscId("psc")
                .registerEntryDate(registerEntryDate)
                .residentialAddress(addressDto)
                .residentialAddressSameAsCorrespondenceAddress(true)
                .build();
    }

    @Test
    void getAddress() {
        assertThat(testDto.getAddress(), is(equalTo(addressDto)));
    }

    @Test
    void getAddressSameAsRegisteredOfficeAddress() {
        assertThat(testDto.getAddressSameAsRegisteredOfficeAddress(), is(equalTo(true)));
    }

    @Test
    void getCountryOfResidence() {
        assertThat(testDto.getCountryOfResidence(), is(equalTo("Wales")));
    }

    @Test
    void getDateOfBirth() {
        assertThat(testDto.getDateOfBirth(), is(equalTo(new Date3TupleDto(12, 9, 1970))));
    }

    @Test
    void getNameElements() {
        assertThat(testDto.getNameElements(), is(equalTo(nameElementsDto)));
    }

    @Test
    void getNaturesOfControl() {
        assertThat(testDto.getNaturesOfControl(), is(equalTo(List.of("type1", "type2"))));
    }

    @Test
    void getNationality() {
        assertThat(testDto.getNationality(), is(equalTo("nationality")));
    }

    @Test
    void getNotifiedOn() {
        assertThat(testDto.getNotifiedOn(), is(equalTo(notifiedOn)));
    }

    @Test
    void getReferenceEtag() {
        assertThat(testDto.getReferenceEtag(), is(equalTo("etag")));
    }

    @Test
    void getReferencePscId() {
        assertThat(testDto.getReferencePscId(), is(equalTo("psc")));
    }

    @Test
    void getCeasedOn() {
        assertThat(testDto.getCeasedOn(), is(equalTo(ceasedOn)));
    }

    @Test
    void getResidentialAddress() {
        assertThat(testDto.getResidentialAddress(), is(equalTo(addressDto)));
    }

    @Test
    void getResidentialAddressSameAsCorrespondenceAddress() {
        assertThat(testDto.getResidentialAddressSameAsCorrespondenceAddress(), is(equalTo(true)));
    }

    @Test
    @DisplayName("toString")
    void testDtoToString() throws JsonProcessingException {
        final String expected = "PscIndividualDto[address=AddressDto[addressLine1='line1', "
                + "addressLine2='line2', careOf='careOf', country='country', locality='locality', "
                + "poBox='poBox', postalCode='postalCode', premises='premises', region='region'], "
                + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2022-11-21, "
                + "naturesOfControl=[type1, type2], notifiedOn=2022-11-10, referenceEtag='etag', "
                + "referencePscId='psc', "
                + "registerEntryDate=2022-11-05, countryOfResidence='Wales', "
                + "dateOfBirth=Date3TupleDto[day=12, month=9, year=1970], "
                + "nameElements=NameElementsDto[forename='forename', otherForenames='other', "
                + "surname='surname', title='title'], nationality='nationality', "
                + "residentialAddress=AddressDto[addressLine1='line1', addressLine2='line2', "
                + "careOf='careOf', country='country', locality='locality', poBox='poBox', "
                + "postalCode='postalCode', premises='premises', region='region'], "
                + "residentialAddressSameAsCorrespondenceAddress=true]";
        assertThat(testDto.toString(), is(expected));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(testDto);

        System.out.println(json);

    }

    @Test
    @DisplayName("WithIdentification toString")
    void testWithIdentificationDtoToString() throws JsonProcessingException {
        var identification = IdentificationDto.builder()
                .countryRegistered("theCountry")
                .placeRegistered("thePlace")
                .legalAuthority("theAuthority")
                .legalForm("theForm")
                .registrationNumber("registration")
                .build();
        var dto = PscWithIdentificationDto.builder()
                .address(addressDto)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(ceasedOn)
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(notifiedOn)
                .referenceEtag("etag")
                .referencePscId("psc")
                .registerEntryDate(registerEntryDate)
                .identification(identification)
                .name("name")
                .build();
        final String expected = "PscWithIdentificationDto[address=AddressDto[addressLine1='line1', "
                + "addressLine2='line2', careOf='careOf', country='country', locality='locality', "
                + "poBox='poBox', postalCode='postalCode', premises='premises', region='region'], "
                + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2022-11-21, "
                + "naturesOfControl=[type1, type2], notifiedOn=2022-11-10, referenceEtag='etag', "
                + "referencePscId='psc', "
                + "registerEntryDate=2022-11-05, "
                + "identification=IdentificationDto[countryRegistered='theCountry',"
                + " placeRegistered='thePlace', registrationNumber='registration',"
                + " legalAuthority='theAuthority', legalForm='theForm'], name='name'"
                + "]";

        assertThat(dto.toString(), is(expected));


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(dto);

        System.out.println(json);

    }

}