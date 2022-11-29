package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

@JsonDeserialize(builder = IdentificationDto.Builder.class)
public class IdentificationDto {

    private String countryRegistered;
    private String placeRegistered;
    private String registrationNumber;
    private String legalAuthority;
    private String legalForm;

    private IdentificationDto() {
        // prevent direct instantiation
    }

    public String getCountryRegistered() {
        return countryRegistered;
    }

    public String getPlaceRegistered() {
        return placeRegistered;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getLegalAuthority() {
        return legalAuthority;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final IdentificationDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<IdentificationDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final IdentificationDto other) {
            this();
            this.countryRegistered(other.getCountryRegistered())
                    .placeRegistered(other.getPlaceRegistered())
                    .registrationNumber(other.getRegistrationNumber())
                    .legalAuthority(other.getLegalAuthority())
                    .legalForm(other.getLegalForm());
        }

        public Builder countryRegistered(final String value) {

            buildSteps.add(data -> data.countryRegistered = value);
            return this;
        }

        public Builder placeRegistered(final String value) {

            buildSteps.add(data -> data.placeRegistered = value);
            return this;
        }

        public Builder registrationNumber(final String value) {

            buildSteps.add(data -> data.registrationNumber = value);
            return this;
        }

        public Builder legalAuthority(final String value) {

            buildSteps.add(data -> data.legalAuthority = value);
            return this;
        }

        public Builder legalForm(final String value) {

            buildSteps.add(data -> data.legalForm = value);
            return this;
        }

        public IdentificationDto build() {

            final var data = new IdentificationDto();
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
        final IdentificationDto that = (IdentificationDto) o;
        return Objects.equals(getCountryRegistered(), that.getCountryRegistered())
                && Objects.equals(getPlaceRegistered(), that.getPlaceRegistered())
                && Objects.equals(getRegistrationNumber(), that.getRegistrationNumber())
                && Objects.equals(getLegalAuthority(), that.getLegalAuthority())
                && Objects.equals(getLegalForm(), that.getLegalForm());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountryRegistered(), getPlaceRegistered(), getRegistrationNumber(),
                getLegalAuthority(), getLegalForm());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IdentificationDto.class.getSimpleName() + "[", "]").add(
                        "countryRegistered='" + countryRegistered + "'")
                .add("placeRegistered='" + placeRegistered + "'")
                .add("registrationNumber='" + registrationNumber + "'")
                .add("legalAuthority='" + legalAuthority + "'")
                .add("legalForm='" + legalForm + "'")
                .toString();
    }
}
