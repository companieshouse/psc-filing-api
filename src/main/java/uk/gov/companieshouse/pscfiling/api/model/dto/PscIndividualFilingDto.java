package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import org.springframework.validation.annotation.Validated;

@JsonDeserialize(builder = PscIndividualFilingDto.Builder.class)
@Validated
public class PscIndividualFilingDto {

    private AddressDto address;
    private Boolean addressSameAsRegisteredOfficeAddress;
    private String countryOfResidence;
    private Date3TupleDto dateOfBirth;
    private String name;
    private NamesElementDto namesElement;
    private List<String> naturesOfControl;
    private String nationality;
    private LocalDate notifiedOn;
    @NotBlank
    private String referenceEtag;
    @NotBlank
    private String referencePscId;
    private String referencePscListEtag;
    @PastOrPresent
    @NotNull
    private LocalDate ceasedOn;
    private AddressDto residentialAddress;
    private Boolean residentialAddressSameAsCorrespondenceAddress;

    private PscIndividualFilingDto() {
    }
    public AddressDto getAddress() {
        return address;
    }

    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return addressSameAsRegisteredOfficeAddress;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public Date3TupleDto getDateOfBirth() {
        return dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public NamesElementDto getNamesElement() {
        return namesElement;
    }

    public List<String> getNaturesOfControl() {
        return naturesOfControl;
    }

    public String getNationality() {
        return nationality;
    }

    public LocalDate getNotifiedOn() {
        return notifiedOn;
    }

    public String getReferenceEtag() {
        return referenceEtag;
    }

    public String getReferencePscId() {
        return referencePscId;
    }

    public String getReferencePscListEtag() {
        return referencePscListEtag;
    }

    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    public AddressDto getResidentialAddress() {
        return residentialAddress;
    }

    public Boolean getResidentialAddressSameAsCorrespondenceAddress() {
        return residentialAddressSameAsCorrespondenceAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PscIndividualFilingDto that = (PscIndividualFilingDto) o;
        return Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getAddressSameAsRegisteredOfficeAddress(),
                        that.getAddressSameAsRegisteredOfficeAddress()) &&
                Objects.equals(getCountryOfResidence(), that.getCountryOfResidence()) &&
                Objects.equals(getDateOfBirth(), that.getDateOfBirth()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getNamesElement(), that.getNamesElement()) &&
                Objects.equals(getNaturesOfControl(), that.getNaturesOfControl()) &&
                Objects.equals(getNationality(), that.getNationality()) &&
                Objects.equals(getNotifiedOn(), that.getNotifiedOn()) &&
                Objects.equals(getReferenceEtag(), that.getReferenceEtag()) &&
                Objects.equals(getReferencePscId(), that.getReferencePscId()) &&
                Objects.equals(getReferencePscListEtag(), that.getReferencePscListEtag()) &&
                Objects.equals(getCeasedOn(), that.getCeasedOn()) &&
                Objects.equals(getResidentialAddress(), that.getResidentialAddress()) &&
                Objects.equals(getResidentialAddressSameAsCorrespondenceAddress(),
                        that.getResidentialAddressSameAsCorrespondenceAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getAddressSameAsRegisteredOfficeAddress(), getCountryOfResidence(),
                getDateOfBirth(), getName(), getNamesElement(), getNaturesOfControl(), getNationality(),
                getNotifiedOn(), getReferenceEtag(), getReferencePscId(), getReferencePscListEtag(), getCeasedOn(),
                getResidentialAddress(), getResidentialAddressSameAsCorrespondenceAddress());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscIndividualFilingDto.class.getSimpleName() + "[", "]").add("address=" + address)
                .add("addressSameAsRegisteredOfficeAddress=" + addressSameAsRegisteredOfficeAddress)
                .add("countryOfResidence='" + countryOfResidence + "'").add("dateOfBirth=" + dateOfBirth)
                .add("name='" + name + "'").add("namesElement=" + namesElement)
                .add("naturesOfControl=" + naturesOfControl).add("nationality='" + nationality + "'")
                .add("notifiedOn=" + notifiedOn).add("referenceEtag='" + referenceEtag + "'")
                .add("referencePscId='" + referencePscId + "'")
                .add("referencePscListEtag='" + referencePscListEtag + "'").add("ceasedOn=" + ceasedOn)
                .add("residentialAddress=" + residentialAddress)
                .add("residentialAddressSameAsCorrespondenceAddress=" + residentialAddressSameAsCorrespondenceAddress)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<PscIndividualFilingDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder address(final AddressDto value) {

            buildSteps.add(data -> data.address = Optional.ofNullable(value)
                    .map(v -> AddressDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {

            buildSteps.add(data -> data.addressSameAsRegisteredOfficeAddress = value);
            return this;
        }

        public Builder countryOfResidence(final String value) {

            buildSteps.add(data -> data.countryOfResidence = value);
            return this;
        }

        public Builder dateOfBirth(final Date3TupleDto value) {

            buildSteps.add(data -> data.dateOfBirth = Optional.ofNullable(value)
                    .map(v -> new Date3TupleDto(v.getDay(), v.getMonth(), v.getYear()))
                    .orElse(null));
            return this;
        }

        public Builder name(final String value) {

            buildSteps.add(data -> data.name = value);
            return this;
        }

        public Builder namesElement(final NamesElementDto value) {

            buildSteps.add(data -> data.namesElement = Optional.ofNullable(value)
                    .map(v -> NamesElementDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder nationality(final String value) {

            buildSteps.add(data -> data.nationality = value);
            return this;
        }

        public Builder naturesOfControl(final List<String> value) {

            buildSteps.add(data -> data.naturesOfControl =
                    Optional.ofNullable(value).map(l -> l.stream().collect(Collectors.toList()))
                            .orElse(null));
            return this;
        }

        public Builder notifiedOn(final LocalDate value) {

            buildSteps.add(data -> data.notifiedOn = value);
            return this;
        }


        public Builder referenceEtag(final String value) {

            buildSteps.add(data -> data.referenceEtag = value);
            return this;
        }

        public Builder referencePscId(final String value) {

            buildSteps.add(data -> data.referencePscId = value);
            return this;
        }

        public Builder referencePscListEtag(final String value) {

            buildSteps.add(data -> data.referencePscListEtag = value);
            return this;
        }

        public Builder ceasedOn(final LocalDate value) {

            buildSteps.add(data -> data.ceasedOn = value);
            return this;
        }

        public Builder residentialAddress(final AddressDto value) {

            buildSteps.add(data -> data.residentialAddress = Optional.ofNullable(value)
                    .map(v -> AddressDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder residentialAddressSameAsCorrespondenceAddress(final Boolean value) {

            buildSteps.add(data -> data.residentialAddressSameAsCorrespondenceAddress = value);
            return this;
        }

        public PscIndividualFilingDto build() {

            final var data = new PscIndividualFilingDto();
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }
    }
}
