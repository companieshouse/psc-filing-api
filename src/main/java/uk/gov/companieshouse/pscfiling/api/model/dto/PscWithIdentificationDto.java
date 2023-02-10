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

@JsonDeserialize(builder = PscWithIdentificationDto.Builder.class)
public class PscWithIdentificationDto implements PscDtoCommunal {

    private final PscDtoCommunal pscCommunal;
    private IdentificationDto identification;
    private String name;

    private PscWithIdentificationDto(final PscCommonDto.Builder commonBuilder) {
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
    public LocalDate getCeasedOn() {
        return pscCommunal.getCeasedOn();
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

    public IdentificationDto getIdentification() {
        return identification;
    }

    public String getName() { return name;}

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PscWithIdentificationDto that = (PscWithIdentificationDto) o;
        return Objects.equals(pscCommunal, that.pscCommunal) && Objects.equals(getIdentification(),
                that.getIdentification());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pscCommunal, getIdentification(), getName());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscWithIdentificationDto.class.getSimpleName() + "[",
                "]").add(pscCommunal.toString())
                .add("identification=" + identification)
                .add("name='" + name +"'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<PscWithIdentificationDto>> buildSteps;
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
            commonBuilder.registerEntryDate(value);
            return this;
        }

        public Builder identification(final IdentificationDto value) {
            buildSteps.add(data -> data.identification = Optional.ofNullable(value)
                    .map(v -> IdentificationDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder name(final String value) {
            buildSteps.add(data -> data.name = value);
            return this;
        }

        public PscWithIdentificationDto build() {

            final var data = new PscWithIdentificationDto(commonBuilder);
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }

    }
}