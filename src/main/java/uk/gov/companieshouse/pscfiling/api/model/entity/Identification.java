package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Identification {
    private String countryRegistered;
    private String legalAuthority;
    private String legalForm;
    private String placeRegistered;
    private String registrationNumber;

    private Identification() {
        // prevent direct instantiation
    }

    public Identification(final String countryRegistered,
                          final String legalAuthority,
                          final String legalForm,
                          final String placeRegistered,
                          final String registrationNumber) {
        this.countryRegistered = countryRegistered;
        this.legalAuthority = legalAuthority;
        this.legalForm = legalForm;
        this.placeRegistered = placeRegistered;
        this.registrationNumber = registrationNumber;
    }

    public String getCountryRegistered() {
        return countryRegistered;
    }

    public String getLegalAuthority() {
        return legalAuthority;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public String getPlaceRegistered() {
        return placeRegistered;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final Identification other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<Identification>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final Identification other) {
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

        public Identification build() {

            final var data = new Identification();
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
        final Identification that = (Identification) o;
        return Objects.equals(getCountryRegistered(), that.getCountryRegistered())
                && Objects.equals(getLegalAuthority(), that.getLegalAuthority())
                && Objects.equals(getLegalForm(), that.getLegalForm())
                && Objects.equals(getPlaceRegistered(), that.getPlaceRegistered())
                && Objects.equals(getRegistrationNumber(), that.getRegistrationNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountryRegistered(), getLegalAuthority(), getLegalForm(),
                getPlaceRegistered(), getRegistrationNumber());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Identification.class.getSimpleName() + "[", "]").add(
                        "countryRegistered='" + countryRegistered + "'")
                .add("legalAuthority='" + legalAuthority + "'")
                .add("legalForm='" + legalForm + "'")
                .add("placeRegistered='" + placeRegistered + "'")
                .add("registrationNumber='" + registrationNumber + "'")
                .toString();
    }
}
