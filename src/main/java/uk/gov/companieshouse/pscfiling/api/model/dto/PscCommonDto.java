package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * A dto to store common fields among PSC types.
 */
public class PscCommonDto implements PscDtoCommunal {
    private AddressDto address;
    private Boolean addressSameAsRegisteredOfficeAddress;
    private LocalDate ceasedOn;
    private NaturesOfControlListDto naturesOfControl;
    private LocalDate notifiedOn;
    private String referenceEtag;
    private String referencePscId;
    private LocalDate registerEntryDate;

    protected PscCommonDto() {
        // prevent direct instantiation
    }

    /**
     * @return The psc address dto
     */
    @Override
    public AddressDto getAddress() {
        return address;
    }

    /**
     * @return True or false depending on if the address matches the
     * registered office address.
     */
    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return addressSameAsRegisteredOfficeAddress;
    }

    /**
     * @return The ceased on date
     */
    @Override
    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    /**
     * @return A list of natures of control.
     */
    @Override
    public List<String> getNaturesOfControl() {
        return Optional.ofNullable(naturesOfControl).map(NaturesOfControlListDto::getList)
                .orElse(null);
    }

    /**
     * @return The notification date.
     */
    @Override
    public LocalDate getNotifiedOn() {
        return notifiedOn;
    }

    /**
     * @return The reference eTag.
     */
    @Override
    public String getReferenceEtag() {
        return referenceEtag;
    }

    /**
     * @return The reference psc ID.
     */
    @Override
    public String getReferencePscId() {
        return referencePscId;
    }

    /**
     * @return The psc register entry date.
     */
    @Override
    public LocalDate getRegisterEntryDate() {
        return registerEntryDate;
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
        return Objects.equals(getAddress(), that.getAddress())
                && Objects.equals(getAddressSameAsRegisteredOfficeAddress(),
                that.getAddressSameAsRegisteredOfficeAddress())
                && Objects.equals(getCeasedOn(), that.getCeasedOn())
                && Objects.equals(getNaturesOfControl(), that.getNaturesOfControl())
                && Objects.equals(getNotifiedOn(), that.getNotifiedOn())
                && Objects.equals(getReferenceEtag(), that.getReferenceEtag())
                && Objects.equals(getReferencePscId(), that.getReferencePscId())
                && Objects.equals(getRegisterEntryDate(), that.getRegisterEntryDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getAddressSameAsRegisteredOfficeAddress(), getCeasedOn(),
                getNaturesOfControl(), getNotifiedOn(), getReferenceEtag(), getReferencePscId(),
                getRegisterEntryDate());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ").add("address=" + address)
                .add("addressSameAsRegisteredOfficeAddress=" + addressSameAsRegisteredOfficeAddress)
                .add("ceasedOn=" + ceasedOn)
                .add("naturesOfControl=" + naturesOfControl)
                .add("notifiedOn=" + notifiedOn)
                .add("referenceEtag='" + referenceEtag + "'")
                .add("referencePscId='" + referencePscId + "'")
                .add("registerEntryDate=" + registerEntryDate)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscCommonDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        protected final List<Consumer<PscCommonDto>> commonBuildSteps;

        public Builder() {
            this.commonBuildSteps = new ArrayList<>();
        }

        public Builder(final PscCommonDto other) {
            this();
            this.address(other.getAddress())
                    .addressSameAsRegisteredOfficeAddress(
                            other.getAddressSameAsRegisteredOfficeAddress())
                    .ceasedOn(other.getCeasedOn())
                    .naturesOfControl(other.getNaturesOfControl())
                    .notifiedOn(other.getNotifiedOn())
                    .referenceEtag(other.getReferenceEtag())
                    .referencePscId(other.getReferencePscId())
                    .registerEntryDate(other.getRegisterEntryDate());
        }

        public Builder address(final AddressDto value) {

            commonBuildSteps.add(data -> data.address = Optional.ofNullable(value)
                    .map(v -> AddressDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {

            commonBuildSteps.add(data -> data.addressSameAsRegisteredOfficeAddress = value);
            return this;
        }

        public Builder ceasedOn(final LocalDate value) {

            commonBuildSteps.add(data -> data.ceasedOn = value);
            return this;
        }

        public Builder naturesOfControl(final List<String> value) {
            commonBuildSteps.add(data -> data.naturesOfControl =
                    Optional.ofNullable(value).map(NaturesOfControlListDto::new).orElse(null));
            return this;
        }

        public Builder notifiedOn(final LocalDate value) {

            commonBuildSteps.add(data -> data.notifiedOn = value);
            return this;
        }

        public Builder referenceEtag(final String value) {

            commonBuildSteps.add(data -> data.referenceEtag = value);
            return this;
        }

        public Builder referencePscId(final String value) {

            commonBuildSteps.add(data -> data.referencePscId = value);
            return this;
        }

        public Builder registerEntryDate(final LocalDate value) {

            commonBuildSteps.add(data -> data.registerEntryDate = value);
            return this;
        }

        public PscCommonDto build() {

            final var data = new PscCommonDto();
            commonBuildSteps.forEach(step -> step.accept(data));


            return data;
        }

    }

}