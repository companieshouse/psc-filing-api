package uk.gov.companieshouse.pscfiling.api.model.dto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.entity.Address;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.Identification;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscFilingWithIdentification;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@ExtendWith(MockitoExtension.class)
class PscIndividualFilingTest {

    private PscIndividualFiling test;
    private PscIndividualFiling.Builder testBuilder;
    private Address address;
    private LocalDate localDate1;
    private Date3Tuple dob1;
    private Instant instant1;
    private NameElements nameElements;
    private Links links;
    private Identification identification;

    @BeforeEach
    void setUp() throws URISyntaxException {
        address = Address.builder()
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
        dob1 = new Date3Tuple(12, 9, 1970);
        instant1 = Instant.parse("2019-11-05T00:00:00Z");
        nameElements = NameElements.builder()
                .forename("forename")
                .otherForenames("other")
                .surname("surname")
                .title("title")
                .build();
        identification = Identification.builder()
                .countryRegistered("theCountry")
                .placeRegistered("thePlace")
                .legalAuthority("theAuthority")
                .legalForm("theForm")
                .registrationNumber("registration")
                .build();
        links = new Links(new URI("self"), new URI("valid"));

        testBuilder = PscIndividualFiling.builder();
        test = testBuilder.id("id")
                .address(address)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(localDate1)
                .createdAt(instant1)
                .countryOfResidence("Wales")
                .dateOfBirth(dob1)
                .etag("etag")
                .kind("kind")
                .links(links)
                .name("individual")
                .nameElements(nameElements)
                .nationality("nationality")
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(localDate1)
                .referenceEtag("etag")
                .referencePscId("psc")
                .referencePscListEtag("list")
                .registerEntryDate(localDate1)
                .residentialAddress(address)
                .residentialAddressSameAsCorrespondenceAddress(true)
                .name("name")
                .statementActionDate(localDate1)
                .statementType("type")
                .updatedAt(instant1)
                .build();
    }

    @Test
    void getAddress() {
        assertThat(test.getAddress(), is(equalTo(address)));
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
    void testToString() throws JsonProcessingException {
        final String expected =
                "PscIndividualFiling[id='id', address=Address[addressLine1='line1', "
                        + "addressLine2='line2', careOf='careOf', country='country', "
                        + "locality='locality', poBox='poBox', postalCode='postalCode', "
                        + "premises='premises', region='region'], "
                        + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2019-11-05, "
                        + "createdAt=2019-11-05T00:00:00Z, etag='etag', kind='kind', "
                        + "links=Links[self=self, validationStatus=valid], name='name', "
                        + "naturesOfControl=[type1, type2], notifiedOn=2019-11-05, "
                        + "referenceEtag='etag', referencePscId='psc', "
                        + "referencePscListEtag='list', registerEntryDate=2019-11-05, "
                        + "updatedAt=2019-11-05T00:00:00Z, countryOfResidence='Wales', "
                        + "dateOfBirth=Date3Tuple[day=12, month=9, year=1970], "
                        + "nameElements=NameElements[forename='forename', otherForenames='other',"
                        + " surname='surname', title='title'], nationality='nationality', "
                        + "residentialAddress=Address[addressLine1='line1', addressLine2='line2',"
                        + " careOf='careOf', country='country', locality='locality', "
                        + "poBox='poBox', postalCode='postalCode', premises='premises', "
                        + "region='region'], residentialAddressSameAsCorrespondenceAddress=true, "
                        + "statementActionDate=2019-11-05, statementType='type']";
        assertThat(test.toString(), is(expected));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(test);

        System.out.println(json);

    }

    @Test
    @DisplayName("WithIdentification toString")
    void testWithIdentificationToString() throws JsonProcessingException {
        final var filing = PscFilingWithIdentification.builder()
                .id("id")
                .address(address)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(localDate1)
                .createdAt(instant1)
                .etag("etag")
                .kind("kind")
                .links(links)
                .name("individual")
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(localDate1)
                .referenceEtag("etag")
                .referencePscId("psc")
                .referencePscListEtag("list")
                .registerEntryDate(localDate1)
                .name("name")
                .updatedAt(instant1)
                .identification(identification)
                .build();
        final String expected = "PscFilingWithIdentification[id='id', "
                + "address=Address[addressLine1='line1', "
                + "addressLine2='line2', careOf='careOf', country='country', "
                + "locality='locality', poBox='poBox', postalCode='postalCode', "
                + "premises='premises', region='region'], "
                + "addressSameAsRegisteredOfficeAddress=true, ceasedOn=2019-11-05, "
                + "createdAt=2019-11-05T00:00:00Z, etag='etag', kind='kind', "
                + "links=Links[self=self, validationStatus=valid], name='name', "
                + "naturesOfControl=[type1, type2], notifiedOn=2019-11-05, "
                + "referenceEtag='etag', referencePscId='psc', "
                + "referencePscListEtag='list', registerEntryDate=2019-11-05, "
                + "updatedAt=2019-11-05T00:00:00Z, "
                + "identification=Identification[countryRegistered='theCountry',"
                + " legalAuthority='theAuthority', legalForm='theForm',"
                + " placeRegistered='thePlace', registrationNumber='registration']"
                + "]";

        assertThat(filing.toString(), is(expected));


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(filing);

        System.out.println(json);

    }


}