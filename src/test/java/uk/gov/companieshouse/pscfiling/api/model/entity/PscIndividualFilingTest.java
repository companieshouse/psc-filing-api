package uk.gov.companieshouse.pscfiling.api.model.entity;

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

@ExtendWith(MockitoExtension.class)
class PscIndividualFilingTest {

    private PscIndividualFiling test;
    private PscIndividualFiling.Builder testBuilder;
    private Address address;
    private LocalDate ceasedOn;
    private LocalDate notifiedOn;
    private LocalDate registerEntryDate;
    private LocalDate statementActionDate;
    private Date3Tuple dob1;
    private Instant createdAt;
    private Instant updatedAt;
    private NameElements nameElements;
    private Links links;


    @BeforeEach
    void setUp() throws URISyntaxException {

        address = createAddress();
        ceasedOn = LocalDate.of(2022, 11, 21);
        createdAt = Instant.parse("2019-11-05T00:00:00Z");
        dob1 = new Date3Tuple(12, 9, 1970);
        links = new Links(new URI("self"), new URI("valid"));
        notifiedOn = LocalDate.of(2022, 11, 10);
        registerEntryDate = LocalDate.of(2022, 11, 5);
        statementActionDate = LocalDate.of(2022, 10, 31);
        nameElements = createNameElements();
        updatedAt = Instant.parse("2022-11-05T00:00:00Z");

        testBuilder = PscIndividualFiling.builder();
        test = createPscIndividualFiling();
    }

    @Test
    void getAddress() {
        assertThat(test.getAddress(), is(equalTo(address)));
    }

    @Test
    void getAddressSameAsRegisteredOfficeAddress() {
        assertThat(test.getAddressSameAsRegisteredOfficeAddress(), is(equalTo(true)));
    }

    @Test
    void getCountryOfResidence() {
        assertThat(test.getCountryOfResidence(), is(equalTo("Wales")));

    }

    @Test
    void getDateOfBirth() {
        assertThat(test.getDateOfBirth(), is(equalTo(dob1)));
    }

    @Test
    void getNameElements() {
        assertThat(test.getNameElements(), is(equalTo(nameElements)));
    }

    @Test
    void getNaturesOfControl() {
        assertThat(test.getNaturesOfControl(), is(equalTo(List.of("type1", "type2"))));
    }

    @Test
    void getNationality() {
        assertThat(test.getNationality(), is(equalTo("nationality")));
    }

    @Test
    void getNotifiedOn() {
        assertThat(test.getNotifiedOn(), is(equalTo(notifiedOn)));
    }

    @Test
    void getReferenceEtag() {
        assertThat(test.getReferenceEtag(), is(equalTo("etag")));
    }

    @Test
    void getReferencePscId() {
        assertThat(test.getReferencePscId(), is(equalTo("psc")));
    }

    @Test
    void getCeasedOn() {
        assertThat(test.getCeasedOn(), is(equalTo(ceasedOn)));
    }

    @Test
    void getResidentialAddress() {
        assertThat(test.getResidentialAddress(), is(equalTo(address)));
    }

    @Test
    void getResidentialAddressSameAsCorrespondenceAddress() {
        assertThat(test.getResidentialAddressSameAsCorrespondenceAddress(), is(equalTo(true)));
    }

    @Test
    @DisplayName("toString")
    void testToString() throws JsonProcessingException {
        final String expected =
                "PscIndividualFiling[id='id', address=Address[addressLine1='line1', "
                        + "addressLine2='line2', careOf='careOf', country='country', "
                        + "locality='locality', poBox='poBox', postalCode='postalCode', "
                        + "premises='premises', region='region'], "
                        + "addressSameAsRegisteredOfficeAddress=true, "
                        + "ceasedOn=2022-11-21, "
                        + "createdAt=2019-11-05T00:00:00Z, etag='etag', kind='kind', "
                        + "links=Links[self=self, validationStatus=valid], "
                        + "naturesOfControl=[type1, type2], notifiedOn=2022-11-10, "
                        + "referenceEtag='etag', referencePscId='psc', "
                        + "registerEntryDate=2022-11-05, "
                        + "updatedAt=2022-11-05T00:00:00Z, countryOfResidence='Wales', "
                        + "dateOfBirth=Date3Tuple[day=12, month=9, year=1970], "
                        + "nameElements=NameElements[forename='forename', otherForenames='other',"
                        + " surname='surname', title='title'], nationality='nationality', "
                        + "residentialAddress=Address[addressLine1='line1', addressLine2='line2',"
                        + " careOf='careOf', country='country', locality='locality', "
                        + "poBox='poBox', postalCode='postalCode', premises='premises', "
                        + "region='region'], residentialAddressSameAsCorrespondenceAddress=true, "
                        + "statementActionDate=2022-10-31, statementType='type']";
        assertThat(test.toString(), is(expected));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(test);

        System.out.println(json);

    }

    private Address createAddress() {
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

        return address;
    }

    private NameElements createNameElements() {
        nameElements = NameElements.builder()
                .forename("forename")
                .otherForenames("other")
                .surname("surname")
                .title("title")
                .build();

        return nameElements;
    }

    private PscIndividualFiling createPscIndividualFiling() {

        test = testBuilder.id("id")
                .address(address)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(ceasedOn)
                .countryOfResidence("Wales")
                .createdAt(createdAt)
                .dateOfBirth(dob1)
                .etag("etag")
                .kind("kind")
                .links(links)
                .nameElements(nameElements)
                .nationality("nationality")
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(notifiedOn)
                .referenceEtag("etag")
                .referencePscId("psc")
                .registerEntryDate(registerEntryDate)
                .residentialAddress(address)
                .residentialAddressSameAsCorrespondenceAddress(true)
                .statementActionDate(statementActionDate)
                .statementType("type")
                .updatedAt(updatedAt)
                .build();

        return test;
    }

}