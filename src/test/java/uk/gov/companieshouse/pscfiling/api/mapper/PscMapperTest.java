package uk.gov.companieshouse.pscfiling.api.mapper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.pscfiling.api.model.dto.AddressDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.Date3TupleDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.NameElementsDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Address;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@ExtendWith(SpringExtension.class)
@Import(PscMapperImpl.class)
class PscMapperTest {

    public static final String SELF_URI =
            "/transactions/197315-203316-322377/persons-with-significant-control/individual"
                    + "/3AftpfAa8RAq7EC3jKC6l7YDJ88=";
    private Address address;
    private AddressDto addressDto;
    private LocalDate localDate1;
    private Date3Tuple dob1;
    private Instant instant1;
    private Links links;
    private NameElements nameElements;
    private NameElementsDto nameElementsDto;

    @Autowired
    private PscMapper testMapper;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .careOf("restrictionsNoticeWithdrawalReason")
                .country("statementActionDate")
                .locality("statementType")
                .poBox("poBox")
                .postalCode("postalCode")
                .premises("premises")
                .region("region")
                .build();
        addressDto = AddressDto.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .careOf("restrictionsNoticeWithdrawalReason")
                .country("statementActionDate")
                .locality("statementType")
                .poBox("poBox")
                .postalCode("postalCode")
                .premises("premises")
                .region("region")
                .build();
        localDate1 = LocalDate.of(2019, 11, 5);
        dob1 = new Date3Tuple(12, 9, 1970);
        instant1 = Instant.parse("2019-11-05T00:00:00Z");
        links = new Links(URI.create(SELF_URI), URI.create(SELF_URI + "validation_status"));
        nameElements = NameElements.builder().title("Mr").forename("Something")
                .otherForenames("Other").surname("Tester").build();
        nameElementsDto = NameElementsDto.builder().title("Mr").forename("Something")
                .otherForenames("Other").surname("Tester").build();
    }

    @Test
    void dtoToPscIndividualFiling() {
        final var dto = PscIndividualDto.builder()
                .address(addressDto)
                .addressSameAsRegisteredOfficeAddress(true)
                .nameElements(nameElementsDto)
                .dateOfBirth(new Date3TupleDto(dob1.getDay(), dob1.getMonth(), dob1.getYear()))
                .countryOfResidence("countryOfResidence")
                .naturesOfControl(List.of("a", "b", "c"))
                .referenceEtag("referenceEtag")
                .referencePscId("referencePscId")
                .nationality("nation")
                .referencePscListEtag("list")
                .residentialAddress(addressDto)
                .residentialAddressSameAsCorrespondenceAddress(true)
                .ceasedOn(localDate1)
                .notifiedOn(localDate1)
                .build();

        final var filing = (PscIndividualFiling) testMapper.map(dto);

        assertThat(filing.getAddress(), is(equalTo(address)));
        assertThat(filing.getAddressSameAsRegisteredOfficeAddress(), is(true));
        assertThat(filing.getNotifiedOn(), is(localDate1.atStartOfDay().toLocalDate()));
        assertThat(filing.getCountryOfResidence(), is("countryOfResidence"));
        assertThat(filing.getCreatedAt(), is(nullValue()));
        assertThat(filing.getDateOfBirth(), is(dob1));
        assertThat(filing.getKind(), is(nullValue()));
        assertThat(filing.getLinks(), is(nullValue()));
        assertThat(filing.getNaturesOfControl(), contains("a", "b", "c"));
        assertThat(filing.getReferenceEtag(), is("referenceEtag"));
        assertThat(filing.getReferencePscId(), is("referencePscId"));
        assertThat(filing.getNationality(), is("nation"));
        assertThat(filing.getReferenceEtag(), is("referenceEtag"));
        assertThat(filing.getReferencePscId(), is("referencePscId"));
        assertThat(filing.getReferencePscListEtag(), is("list"));
        assertThat(filing.getCeasedOn(), is(localDate1.atStartOfDay().toLocalDate()));
        assertThat(filing.getResidentialAddress(), is(equalTo(address)));
        assertThat(filing.getResidentialAddressSameAsCorrespondenceAddress(), is(true));
        assertThat(filing.getUpdatedAt(), is(nullValue()));
    }

    @Test
    void nullDtoToPscIndividualFiling() {
        final var filing = testMapper.map((PscIndividualDto) null);

        assertThat(filing, is(nullValue()));
    }

    @Test
    void emptyDtoToPscIndividualFiling() {
        final var dto = PscIndividualDto.builder().naturesOfControl(Arrays.asList(null, null))
                .build();
        final var emptyFiling =
                PscIndividualFiling.builder().naturesOfControl(Arrays.asList(null, null))
                        .build();

        final var filing = (PscIndividualFiling) testMapper.map(dto);

        assertThat(filing, is(equalTo(emptyFiling)));
        assertThat(filing.getAddressSameAsRegisteredOfficeAddress(), is(nullValue()));
        assertThat(filing.getResidentialAddressSameAsCorrespondenceAddress(), is(nullValue()));
    }

    @Test
    void emptyPscNullNaturesOfControlDtoToPscFiling() {
        final var dto = PscIndividualDto.builder().naturesOfControl(null)
                .build();
        final var emptyFiling = PscIndividualFiling.builder().naturesOfControl(null)
                .build();

        final var filing = testMapper.map(dto);

        assertThat(filing, is(equalTo(emptyFiling)));
        assertThat(filing.getNaturesOfControl(), is(nullValue()));
    }

    @Test
    void pscIndividualFilingToDto() {
        final PscIndividualFiling filing = PscIndividualFiling.builder()
                .address(address)
                .addressSameAsRegisteredOfficeAddress(true)
                .notifiedOn(localDate1)
                .countryOfResidence("countryOfResidence")
                .createdAt(instant1)
                .dateOfBirth(dob1)
                .naturesOfControl(List.of("a", "b", "c"))
                .kind("kind")
                .links(links)
                .nameElements(nameElements)
                .nationality("nation")
                .referenceEtag("referenceEtag")
                .referencePscId("referencePscId")
                .referencePscListEtag("list")
                .residentialAddress(address)
                .residentialAddressSameAsCorrespondenceAddress(true)
                .ceasedOn(localDate1)
                .updatedAt(instant1)
                .statementActionDate(localDate1)
                .statementType("type1")
                .build();

        final var dto = (PscIndividualDto) testMapper.map(filing);

        assertThat(dto.getAddress(), is(equalTo(addressDto)));
        assertThat(dto.getAddressSameAsRegisteredOfficeAddress(), is(true));
        assertThat(dto.getNotifiedOn(), is(localDate1));
        assertThat(dto.getCountryOfResidence(), is("countryOfResidence"));
        assertThat(dto.getDateOfBirth(),
                is(new Date3TupleDto(dob1.getDay(), dob1.getMonth(), dob1.getYear())));
        assertThat(dto.getNameElements(), is(nameElementsDto));
        assertThat(dto.getNaturesOfControl(), contains("a", "b", "c"));
        assertThat(dto.getReferenceEtag(), is("referenceEtag"));
        assertThat(dto.getReferencePscId(), is("referencePscId"));
        assertThat(dto.getNationality(), is("nation"));
        assertThat(dto.getReferencePscListEtag(), is("list"));
        assertThat(dto.getResidentialAddress(), is(equalTo(addressDto)));
        assertThat(dto.getResidentialAddressSameAsCorrespondenceAddress(), is(true));
        assertThat(dto.getCeasedOn(), is(localDate1));
    }

    @Test
    void nullPscIndividualFilingNaturesOfControlToDto() {
        final var dto = testMapper.map((PscIndividualFiling) null);

        assertThat(dto, is(nullValue()));
    }

    @Test
    void emptyPscIndividualFilingNullNaturesOfControlToDto() {
        final var filing = PscIndividualFiling.builder()
                .build();
        final var emptyDto = PscIndividualDto.builder()
                .build();

        final var dto = testMapper.map(filing);

        assertThat(dto, is(equalTo(emptyDto)));
    }

    @Test
    void emptyPscIndividualFilingNullNatureOfControlToDto() {
        final var filing =
                PscIndividualFiling.builder().naturesOfControl(Collections.singletonList(null))
                        .build();
        final var emptyDto =
                PscIndividualDto.builder().naturesOfControl(Collections.singletonList(null))
                        .build();

        final var dto = testMapper.map(filing);

        assertThat(dto, is(equalTo(emptyDto)));
    }

    @Test
    void emptyPscIndividualFilingToDto() {
        final var filing = PscIndividualFiling.builder().naturesOfControl(Collections.emptyList())
                .build();
        final var emptyDto = PscIndividualDto.builder().naturesOfControl(Collections.emptyList())
                .build();

        final var dto = (PscIndividualDto) testMapper.map(filing);

        assertThat(dto, is(equalTo(emptyDto)));
        assertThat(dto.getAddressSameAsRegisteredOfficeAddress(), is(nullValue()));
        assertThat(dto.getResidentialAddressSameAsCorrespondenceAddress(), is(nullValue()));
    }

    @Test
    void isoDateOfBirth() {
        final var tuple = new Date3Tuple(dob1.getDay(), dob1.getMonth(), dob1.getYear());

        final var isoDateOfBirth = testMapper.isoDateOfBirth(tuple);

        assertThat(isoDateOfBirth, is("1970-09-12"));
    }

    @Test
    void nullIsoDateOfBirth() {
        final var isoDateOfBirth = testMapper.isoDateOfBirth(null);

        assertThat(isoDateOfBirth, is(nullValue()));
    }
}

