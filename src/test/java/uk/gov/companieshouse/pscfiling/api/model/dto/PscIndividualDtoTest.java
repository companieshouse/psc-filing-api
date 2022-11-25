package uk.gov.companieshouse.pscfiling.api.model.dto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
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
    private PscIndividualDto.Builder testBuilder;
    private AddressDto addressDto;
    private LocalDate localDate1;
    private Date3TupleDto dob1;
    private Instant instant1;
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
        localDate1 = LocalDate.of(2019, 11, 5);
        dob1 = new Date3TupleDto(12, 9, 1970);
        instant1 = Instant.parse("2019-11-05T00:00:00Z");
        nameElementsDto = NameElementsDto.builder()
                .forename("forename")
                .otherForenames("other")
                .surname("surname")
                .title("title")
                .build();

        testBuilder = PscIndividualDto.builder();
        testDto = testBuilder.address(addressDto)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(localDate1)
                .countryOfResidence("Wales")
                .dateOfBirth(dob1)
                .name("individual")
                .nameElements(nameElementsDto)
                .nationality("nationality")
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(localDate1)
                .referenceEtag("etag")
                .referencePscId("psc")
                .referencePscListEtag("list")
                .registerEntryDate(localDate1)
                .residentialAddress(addressDto)
                .residentialAddressSameAsCorrespondenceAddress(true)
                .name("name")
                .build();
    }

    @Test
    void getAddress() {
        assertThat(testDto.getAddress(), is(equalTo(addressDto)));
    }

    @Test
    void getAddressSameAsRegisteredOfficeAddress() {
    }

    @Test
    void getCountryOfResidence() {
    }

    @Test
    void getDateOfBirth() {
    }

    @Test
    void getName() {
    }

    @Test
    void getNameElements() {
    }

    @Test
    void getNaturesOfControl() {
    }

    @Test
    void getNationality() {
    }

    @Test
    void getNotifiedOn() {
    }

    @Test
    void getReferenceEtag() {
    }

    @Test
    void getReferencePscId() {
    }

    @Test
    void getReferencePscListEtag() {
    }

    @Test
    void getCeasedOn() {
    }

    @Test
    void getResidentialAddress() {
    }

    @Test
    void getResidentialAddressSameAsCorrespondenceAddress() {
    }

    @Test
    @DisplayName("toString")
    void testDtoToString() throws JsonProcessingException {
        final String expected = "PscIndividualDto[address=AddressDto[addressLine1='line1', addressLine2='line2', careOf='careOf', country='country', locality='locality', poBox='poBox', postalCode='postalCode', premises='premises', region='region'], "
                + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2019-11-05, name='name', "
                + "naturesOfControl=[type1, type2], notifiedOn=2019-11-05, referenceEtag='etag', "
                + "referencePscId='psc', referencePscListEtag='list', "
                + "registerEntryDate=2019-11-05, countryOfResidence='Wales', "
                + "dateOfBirth=Date3TupleDto[day=12, month=9, year=1970], "
                + "nameElements=NameElementsDto[forename='forename', otherForenames='other', surname='surname', title='title'], "
                + "nationality='nationality', "
                + "residentialAddress=AddressDto[addressLine1='line1', addressLine2='line2', careOf='careOf', country='country', locality='locality', poBox='poBox', postalCode='postalCode', premises='premises', region='region'], "
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
                .ceasedOn(localDate1)
                .name("psc with identification")
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(localDate1)
                .referenceEtag("etag")
                .referencePscId("psc")
                .referencePscListEtag("list")
                .registerEntryDate(localDate1)
                .identification(identification).build();
        final String expected = "PscWithIdentificationDto[address=AddressDto[addressLine1='line1', addressLine2='line2', careOf='careOf', country='country', locality='locality', poBox='poBox', postalCode='postalCode', premises='premises', region='region'], "
                + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2019-11-05, name='psc with identification', "
                + "naturesOfControl=[type1, type2], notifiedOn=2019-11-05, referenceEtag='etag', "
                + "referencePscId='psc', referencePscListEtag='list', "
                + "registerEntryDate=2019-11-05, "
                + "identification=IdentificationDto[countryRegistered='theCountry',"
                + " placeRegistered='thePlace', registrationNumber='registration',"
                + " legalAuthority='theAuthority', legalForm='theForm']"
                + "]";

        assertThat(dto.toString(), is(expected));


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(dto);

        System.out.println(json);

    }


}