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

class WithIdentificationFilingDataDtoTest {

    private PscWithIdentificationDto testDto;
    private AddressDto addressDto;
    private LocalDate ceasedOn;
    private LocalDate notifiedOn;
    private LocalDate registerEntryDate;

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
        notifiedOn = LocalDate.of(2022,11,10);
        registerEntryDate = LocalDate.of(2022,11,5);
        PscWithIdentificationDto.Builder testBuilder = PscWithIdentificationDto.builder();
        testDto = testBuilder.address(addressDto)
            .addressSameAsRegisteredOfficeAddress(true)
            .ceasedOn(ceasedOn)
            .naturesOfControl(List.of("type1", "type2"))
            .notifiedOn(notifiedOn)
            .referenceEtag("etag")
            .referencePscId("psc")
            .registerEntryDate(registerEntryDate)
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
    void getNaturesOfControl() {
        assertThat(testDto.getNaturesOfControl(), is(equalTo(List.of("type1", "type2"))));
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
    @DisplayName("toString")
    void testDtoToString() throws JsonProcessingException {
        final String expected = "PscWithIdentificationDto[address=AddressDto[addressLine1='line1', "
            + "addressLine2='line2', careOf='careOf', country='country', locality='locality', "
            + "poBox='poBox', postalCode='postalCode', premises='premises', region='region'], "
            + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2022-11-21, "
            + "naturesOfControl=[type1, type2], notifiedOn=2022-11-10, referenceEtag='etag', "
            + "referencePscId='psc', registerEntryDate=2022-11-05, "
            + "identification=null, name='null']";
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