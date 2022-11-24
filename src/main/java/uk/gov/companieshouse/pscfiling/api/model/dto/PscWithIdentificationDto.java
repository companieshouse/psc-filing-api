package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class PscWithIdentificationDto implements PscCommon {

    private final PscCommon pscCommon;
    private IdentificationDto identification;

    private PscWithIdentificationDto(final PscCommonDto.Builder commonBuilder) {
        Objects.requireNonNull(commonBuilder);
        pscCommon = commonBuilder.build();
    }

    @Override
    public AddressDto getAddress() {
        return pscCommon.getAddress();
    }

    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return pscCommon.getAddressSameAsRegisteredOfficeAddress();
    }

    @Override
    public String getName() {
        return pscCommon.getName();
    }

    @Override
    public List<String> getNaturesOfControl() {
        return pscCommon.getNaturesOfControl();
    }

    @Override
    public LocalDate getNotifiedOn() {
        return pscCommon.getNotifiedOn();
    }

    @Override
    public String getReferenceEtag() {
        return pscCommon.getReferenceEtag();
    }

    @Override
    public String getReferencePscId() {
        return pscCommon.getReferencePscId();
    }

    @Override
    public String getReferencePscListEtag() {
        return pscCommon.getReferencePscListEtag();
    }

    @Override
    public LocalDate getCeasedOn() {
        return pscCommon.getCeasedOn();
    }


    public IdentificationDto getIdentification() {
        return identification;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PscWithIdentificationDto that = (PscWithIdentificationDto) o;
        return Objects.equals(getIdentification(), that.getIdentification());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentification());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscWithIdentificationDto.class.getSimpleName() + "[",
                "]").add("identification=" + identification).toString();
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

        public Builder name(final String value) {
            commonBuilder.name(value);
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

        public Builder ceasedOn(final LocalDate value) {
            commonBuilder.ceasedOn(value);
            return this;
        }

        public Builder identification(final IdentificationDto value) {
            buildSteps.add(data -> data.identification = Optional.ofNullable(value)
                    .map(v -> IdentificationDto.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public PscWithIdentificationDto build() {

            final var data = new PscWithIdentificationDto(commonBuilder);
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }

    }
}
