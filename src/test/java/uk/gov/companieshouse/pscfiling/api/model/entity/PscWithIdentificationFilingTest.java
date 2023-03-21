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

class PscWithIdentificationFilingTest {

    private PscWithIdentificationFiling test;
    private PscWithIdentificationFiling.Builder testBuilder;
    private Identification identification;
    private Address address;
    private LocalDate ceasedOn;
    private LocalDate notifiedOn;
    private LocalDate registerEntryDate;
    private Instant createdAt;
    private Instant updatedAt;
    private Links links;
    private LocalDate statementActionDate;
    private String id;
    private String name;
    private String statementType;

    PscWithIdentificationFilingTest() {
    }

    @BeforeEach
    void setUp() throws URISyntaxException {

        address = createAddress();
        name = "name";
        identification = createIdentification();
        ceasedOn = LocalDate.of(2022, 11, 21);
        createdAt = Instant.parse("2019-11-05T00:00:00Z");
        links = new Links(new URI("self"), new URI("validationStatus"));
        notifiedOn = LocalDate.of(2022, 11, 10);
        registerEntryDate = LocalDate.of(2022, 11, 5);
        statementActionDate = LocalDate.of(2022, 10, 31);
        statementType = "type";
        updatedAt = Instant.parse("2022-11-05T00:00:00Z");
        id = "id";
        testBuilder = PscWithIdentificationFiling.builder();
        test = createPscWithIdentificationFiling();
    }

    @Test
    void getId() {
        assertThat(test.getId(), is(equalTo(id)));
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
    void getCeasedOn() {
        assertThat(test.getCeasedOn(), is(equalTo(ceasedOn)));
    }

    @Test
    void getName() {
        assertThat(test.getName(), is(equalTo(name)));
    }

    @Test
    void getCreatedAt() {
        assertThat(test.getCreatedAt(), is(equalTo(createdAt)));
    }

    @Test
    void getReferenceEtag() {
        assertThat(test.getReferenceEtag(), is(equalTo("etag")));
    }

    @Test
    void getKind() {
        assertThat(test.getKind(), is(equalTo("kind")));
    }

    @Test
    void getLinks() {
        assertThat(test.getLinks(), is(equalTo(links)));
    }

    @Test
    void getNaturesOfControl() {
        assertThat(test.getNaturesOfControl(), is(equalTo(List.of("type1", "type2"))));
    }

    @Test
    void getNotifiedOn() {
        {
            assertThat(test.getNotifiedOn(), is(equalTo(notifiedOn)));
        }
    }

    @Test
    void getReferencePscId() {
        assertThat(test.getReferencePscId(), is(equalTo("psc")));
    }

    @Test
    void getReferencePscListEtag() {
        assertThat(test.getReferencePscListEtag(), is(equalTo("list")));
    }

    @Test
    void getRegisterEntryDate() {
        assertThat(test.getRegisterEntryDate(), is(equalTo(registerEntryDate)));
    }

    @Test
    void getUpdatedAt() {
        assertThat(test.getUpdatedAt(), is(equalTo(updatedAt)));
    }

    @Test
    void getIdentification() {
        assertThat(test.getIdentification(), is(equalTo(identification)));
    }

    @Test
    void getStatementActionDate() {
        assertThat(test.getStatementActionDate(), is(equalTo(statementActionDate)));
    }

    @Test
    void getStatementType() {
        assertThat(test.getStatementType(), is(equalTo(statementType)));
    }

    @Test
    @DisplayName("toString")
    void testToString() throws JsonProcessingException {
        final String expected =
                "PscWithIdentificationFiling[id='id', address=Address[addressLine1='line1', "
                        + "addressLine2='line2', careOf='careOf', country='country', "
                        + "locality='locality', poBox='poBox', postalCode='postalCode', "
                        + "premises='premises', region='region'], "
                        + "addressSameAsRegisteredOfficeAddress=true, name=name, "
                        + "ceasedOn=2022-11-21, createdAt=2019-11-05T00:00:00Z, etag='etag', "
                        + "kind='kind', links=Links[self=self, "
                        + "validationStatus=validationStatus], naturesOfControl=[type1, type2], "
                        + "notifiedOn=2022-11-10, referenceEtag='etag', referencePscId='psc', "
                        + "referencePscListEtag='list', registerEntryDate=2022-11-05, "
                        + "updatedAt=2022-11-05T00:00:00Z, "
                        + "identification='Identification[countryRegistered='theCountry', "
                        + "legalAuthority='theAuthority', legalForm='theForm', "
                        + "placeRegistered='thePlace', registrationNumber='registration']', "
                        + "statementActionDate=2022-10-31, statementType='type']";
        assertThat(test.toString(), is(expected));

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        final var json = mapper.writeValueAsString(test);

        System.out.println(json);
    }

//    @Test
//    void builder() {
//    }
//
//    @Test
//    void testBuilder() {
//    }

    private PscWithIdentificationFiling createPscWithIdentificationFiling() {

        test = testBuilder.id("id")
                .address(address)
                .addressSameAsRegisteredOfficeAddress(true)
                .ceasedOn(ceasedOn)
                .createdAt(createdAt)
                .etag("etag")
                .kind("kind")
                .links(links)
                .identification(identification)
                .name(name)
                .naturesOfControl(List.of("type1", "type2"))
                .notifiedOn(notifiedOn)
                .referenceEtag("etag")
                .referencePscId("psc")
                .referencePscListEtag("list")
                .registerEntryDate(registerEntryDate)
                .statementActionDate(statementActionDate)
                .statementType("type")
                .updatedAt(updatedAt)
                .build();

        return test;
    }

    private Identification createIdentification() {
        identification = Identification.builder()
                .countryRegistered("theCountry")
                .placeRegistered("thePlace")
                .legalAuthority("theAuthority")
                .legalForm("theForm")
                .registrationNumber("registration")
                .build();

        return identification;
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
}