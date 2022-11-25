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
import org.springframework.validation.annotation.Validated;

@JsonDeserialize(builder = PscIndividualDto.Builder.class)
@Validated
public class PscIndividualDto implements PscDtoCommunal {

    private final PscDtoCommunal pscCommunal;
    private String countryOfResidence;
    private Date3TupleDto dateOfBirth;
    private NameElementsDto nameElements;
    private String nationality;
    private AddressDto residentialAddress;
    private Boolean residentialAddressSameAsCorrespondenceAddress;

    private PscIndividualDto(final PscCommonDto.Builder commonBuilder) {
        Objects.requireNonNull(commonBuilder);
        pscCommunal = commonBuilder.build();
    }

    @Override
    public AddressDto getAddress() {
        return pscCommunal.getAddress();
    }

    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return pscCommunal.getAddressSameAsRegisteredOfficeAddress();
    }

    @Override
    public String getName() {
        return pscCommunal.getName();
    }

    @Override
    public List<String> getNaturesOfControl() {
        return pscCommunal.getNaturesOfControl();
    }

    @Override
    public LocalDate getNotifiedOn() {
        return pscCommunal.getNotifiedOn();
    }

    @Override
    public String getReferenceEtag() {
        return pscCommunal.getReferenceEtag();
    }

    @Override
    public String getReferencePscId() {
        return pscCommunal.getReferencePscId();
    }

    @Override
    public String getReferencePscListEtag() {
        return pscCommunal.getReferencePscListEtag();
    }

    @Override
    public LocalDate getRegisterEntryDate() {
        return pscCommunal.getRegisterEntryDate();
    }

    @Override
    public LocalDate getCeasedOn() {
        return pscCommunal.getCeasedOn();
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public Date3TupleDto getDateOfBirth() {
        return dateOfBirth;
    }

    public NameElementsDto getNameElements() {
        return nameElements;
    }

    public String getNationality() {
        return nationality;
    }

    public AddressDto getResidentialAddress() {
        return residentialAddress;
    }

    public Boolean getResidentialAddressSameAsCorrespondenceAddress() {
        return residentialAddressSameAsCorrespondenceAddress;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PscIndividualDto that = (PscIndividualDto) o;
        return Objects.equals(pscCommunal, that.pscCommunal)
                && Objects.equals(getCountryOfResidence(), that.getCountryOfResidence())
                && Objects.equals(getDateOfBirth(), that.getDateOfBirth())
                && Objects.equals(getNameElements(), that.getNameElements())
                && Objects.equals(getNationality(), that.getNationality())
                && Objects.equals(getResidentialAddress(), that.getResidentialAddress())
                && Objects.equals(getResidentialAddressSameAsCorrespondenceAddress(),
                that.getResidentialAddressSameAsCorrespondenceAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pscCommunal, getCountryOfResidence(), getDateOfBirth(),
                getNameElements(), getNationality(), getResidentialAddress(),
                getResidentialAddressSameAsCorrespondenceAddress());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscIndividualDto.class.getSimpleName() + "[", "]").add(
                        pscCommunal.toString())
                .add("countryOfResidence='" + countryOfResidence + "'")
                .add("dateOfBirth=" + dateOfBirth)
                .add("nameElements=" + nameElements)
                .add("nationality='" + nationality + "'")
                .add("residentialAddress=" + residentialAddress)
                .add("residentialAddressSameAsCorrespondenceAddress="
                        + residentialAddressSameAsCorrespondenceAddress)
                .toString();

    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<PscIndividualDto>> buildSteps;
        private final PscCommonDto.Builder commonBuilder = PscCommonDto.builder();

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder address(final AddressDto value) {
            commonBuilder.address(value);
            return this;
        }

        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {
            commonBuilder.addressSameAsRegisteredOfficeAddress(value);
            return this;
        }

        public Builder ceasedOn(final LocalDate value) {
            commonBuilder.ceasedOn(value);
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
            commonBuilder.name(value);
            return this;
        }

        public Builder nameElements(final NameElementsDto value) {

            buildSteps.add(data -> data.nameElements = Optional.ofNullable(value)
                    .map(v -> NameElementsDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder nationality(final String value) {

            buildSteps.add(data -> data.nationality = value);
            return this;
        }

        public Builder naturesOfControl(final List<String> value) {
            commonBuilder.naturesOfControl(value);
            return this;
        }

        public Builder notifiedOn(final LocalDate value) {
            commonBuilder.notifiedOn(value);
            return this;
        }


        public Builder referenceEtag(final String value) {
            commonBuilder.referenceEtag(value);
            return this;
        }

        public Builder referencePscId(final String value) {
            commonBuilder.referencePscId(value);
            return this;
        }

        public Builder referencePscListEtag(final String value) {
            commonBuilder.referencePscListEtag(value);
            return this;
        }

        public Builder registerEntryDate(final LocalDate value) {
            commonBuilder.registerEntyDate(value);
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

        public PscIndividualDto build() {

            final var data = new PscIndividualDto(commonBuilder);
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }
    }
}
