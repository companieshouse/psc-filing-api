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
public class PscWithIdentificationDto extends PscCommonDto implements PscDtoCommunal {
    private IdentificationDto identification;
    private String name;

    private PscWithIdentificationDto() {
        // prevent instantiation
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
        if (!super.equals(o)) {
            return false;
        }
        final PscWithIdentificationDto that = (PscWithIdentificationDto) o;
        return Objects.equals(getIdentification(), that.getIdentification()) && Objects.equals(
                getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIdentification(), getName());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscWithIdentificationDto.class.getSimpleName() + "[",
                "]").add(super.toString())
                .add("identification=" + identification)
                .add("name='" + name +"'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends PscCommonDto.Builder {

        private final List<Consumer<PscWithIdentificationDto>> buildSteps;
        private final PscCommonDto.Builder commonBuilder = PscCommonDto.builder();

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        @Override
        public Builder address(final AddressDto value) {
            commonBuilder.address(value);
            return this;
        }

        @Override
        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {
            commonBuilder.addressSameAsRegisteredOfficeAddress(value);
            return this;
        }

        @Override
        public Builder ceasedOn(final LocalDate value) {
            commonBuilder.ceasedOn(value);
            return this;
        }

        @Override
        public Builder naturesOfControl(final List<String> value) {
            commonBuilder.naturesOfControl(value);
            return this;
        }

        @Override
        public Builder notifiedOn(final LocalDate value) {
            commonBuilder.notifiedOn(value);
            return this;
        }

        @Override
        public Builder referenceEtag(final String value) {
            commonBuilder.referenceEtag(value);
            return this;
        }

        @Override
        public Builder referencePscId(final String value) {
            commonBuilder.referencePscId(value);
            return this;
        }

        @Override
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

        @Override
        public PscWithIdentificationDto build() {

            final var data = new PscWithIdentificationDto();
            commonBuilder.commonBuildSteps.forEach(s -> s.accept(data));
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }

    }
}