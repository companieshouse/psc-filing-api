package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithIdentificationFilingDataDto implements FilingDtoCommunal {

    private String countryRegistered;
    private String placeRegistered;
    private String registrationNumber;
    private String legalAuthority;
    private String legalForm;
    private String ceasedOn;

    private String registerEntryDate;

    public WithIdentificationFilingDataDto() {
        // prevent direct instantiation
    }

    public String getCeasedOn() {
        return ceasedOn;
    }

    public String getRegisterEntryDate() {
        return registerEntryDate;
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

    public static Builder builder(final WithIdentificationFilingDataDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<WithIdentificationFilingDataDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final WithIdentificationFilingDataDto other) {
            this();
            this.countryRegistered(other.getCountryRegistered())
                    .placeRegistered(other.getPlaceRegistered())
                    .registrationNumber(other.getRegistrationNumber())
                    .legalAuthority(other.getLegalAuthority())
                    .legalForm(other.getLegalForm())
                    .ceasedOn(other.getCeasedOn())
                    .registerEntryDate(other.getRegisterEntryDate());
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

        public Builder ceasedOn(final String value) {

            buildSteps.add(data -> data.ceasedOn = value);
            return this;
        }

        public Builder registerEntryDate(final String value) {

            buildSteps.add(data -> data.registerEntryDate = value);
            return this;
        }


        public WithIdentificationFilingDataDto build() {

            final var data = new WithIdentificationFilingDataDto();
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
        final WithIdentificationFilingDataDto that = (WithIdentificationFilingDataDto) o;
        return Objects.equals(getCountryRegistered(), that.getCountryRegistered())
                && Objects.equals(getPlaceRegistered(), that.getPlaceRegistered())
                && Objects.equals(getRegistrationNumber(), that.getRegistrationNumber())
                && Objects.equals(getLegalAuthority(), that.getLegalAuthority())
                && Objects.equals(getLegalForm(), that.getLegalForm())
                && Objects.equals(getCeasedOn(), that.getCeasedOn())
                && Objects.equals(getRegisterEntryDate(), that.getRegisterEntryDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCountryRegistered(), getPlaceRegistered(), getRegistrationNumber(),
            getLegalAuthority(), getLegalForm(), getCeasedOn(), getRegisterEntryDate());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WithIdentificationFilingDataDto.class.getSimpleName() + "[", "]")
                .add("countryRegistered='" + countryRegistered + "'")
                .add("placeRegistered='" + placeRegistered + "'")
                .add("registrationNumber='" + registrationNumber + "'")
                .add("legalAuthority='" + legalAuthority + "'")
                .add("legalForm='" + legalForm + "'")
                .add("ceasedOn='" + ceasedOn + "'")
                .add("registerEntryDate='" + registerEntryDate + "'")
                .toString();
    }
}
