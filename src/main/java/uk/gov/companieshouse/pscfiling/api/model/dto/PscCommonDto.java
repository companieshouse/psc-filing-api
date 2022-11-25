package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

public class PscCommonDto implements PscDtoCommunal {
    private AddressDto address;
    private Boolean addressSameAsRegisteredOfficeAddress;
    @PastOrPresent
    @NotNull
    private LocalDate ceasedOn;
    private String name;
    private NaturesOfControlListDto naturesOfControl;
    private LocalDate notifiedOn;
    @NotBlank
    private String referenceEtag;
    @NotBlank
    private String referencePscId;
    private String referencePscListEtag;
    private LocalDate registerEntryDate;

    private PscCommonDto() {
        // prevent direct instantiation
    }

    @Override
    public AddressDto getAddress() {
        return address;
    }

    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return addressSameAsRegisteredOfficeAddress;
    }

    @Override
    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getNaturesOfControl() {
        return Optional.ofNullable(naturesOfControl).map(NaturesOfControlListDto::getList)
                .orElse(null);
    }

    @Override
    public LocalDate getNotifiedOn() {
        return notifiedOn;
    }

    @Override
    public String getReferenceEtag() {
        return referenceEtag;
    }

    @Override
    public String getReferencePscId() {
        return referencePscId;
    }

    @Override
    public String getReferencePscListEtag() {
        return referencePscListEtag;
    }

    @Override
    public LocalDate getRegisterEntryDate() {
        return registerEntryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscCommonDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<PscCommonDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final PscCommonDto other) {
            this();
            this.address(other.getAddress())
                    .addressSameAsRegisteredOfficeAddress(
                            other.getAddressSameAsRegisteredOfficeAddress())
                    .ceasedOn(other.getCeasedOn())
                    .name(other.getName())
                    .naturesOfControl(other.getNaturesOfControl())
                    .notifiedOn(other.getNotifiedOn())
                    .referenceEtag(other.getReferenceEtag())
                    .referencePscId(other.getReferencePscId())
                    .referencePscListEtag(other.getReferencePscListEtag())
                    .registerEntyDate(other.getRegisterEntryDate());
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

        public Builder ceasedOn(final LocalDate value) {

            buildSteps.add(data -> data.ceasedOn = value);
            return this;
        }

        public Builder name(final String value) {

            buildSteps.add(data -> data.name = value);
            return this;
        }

        public Builder naturesOfControl(final List<String> value) {
            buildSteps.add(data -> data.naturesOfControl =
                    Optional.ofNullable(value).map(NaturesOfControlListDto::new).orElse(null));
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

        public Builder registerEntyDate(final LocalDate value) {

            buildSteps.add(data -> data.registerEntryDate = value);
            return this;
        }

        public PscCommonDto build() {

            final var data = new PscCommonDto();
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PscCommonDto that = (PscCommonDto) o;
        return Objects.equals(getAddress(), that.getAddress()) && Objects.equals(
                getAddressSameAsRegisteredOfficeAddress(),
                that.getAddressSameAsRegisteredOfficeAddress()) && Objects.equals(getCeasedOn(),
                that.getCeasedOn()) && Objects.equals(getName(), that.getName()) && Objects.equals(
                getNaturesOfControl(), that.getNaturesOfControl()) && Objects.equals(
                getNotifiedOn(), that.getNotifiedOn()) && Objects.equals(getReferenceEtag(),
                that.getReferenceEtag()) && Objects.equals(getReferencePscId(),
                that.getReferencePscId()) && Objects.equals(getReferencePscListEtag(),
                that.getReferencePscListEtag()) && Objects.equals(getRegisterEntryDate(),
                that.getRegisterEntryDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getAddressSameAsRegisteredOfficeAddress(), getCeasedOn(),
                getName(), getNaturesOfControl(), getNotifiedOn(), getReferenceEtag(),
                getReferencePscId(), getReferencePscListEtag(), getRegisterEntryDate());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ").add("address=" + address)
                .add("addressSameAsRegisteredOfficeAddress=" + addressSameAsRegisteredOfficeAddress)
                .add("ceasedOn=" + ceasedOn)
                .add("name='" + name + "'")
                .add("naturesOfControl=" + naturesOfControl)
                .add("notifiedOn=" + notifiedOn)
                .add("referenceEtag='" + referenceEtag + "'")
                .add("referencePscId='" + referencePscId + "'")
                .add("referencePscListEtag='" + referencePscListEtag + "'")
                .add("registerEntryDate=" + registerEntryDate)
                .toString();
    }
}