package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilingDataDto {

    private String title;
    private String firstName;
    private String otherForenames;
    private String lastName;
    private String dateOfBirth;
    private String ceasedOn;
    private String registerEntryDate;

    public FilingDataDto() {
        // prevent direct instantiation
    }

    public String getTitle() {
        return title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCeasedOn() {
        return ceasedOn;
    }

    public String getRegisterEntryDate() {
        return registerEntryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final FilingDataDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<FilingDataDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final FilingDataDto other) {
            this();
            this.title(other.getTitle())
                .firstName(other.getFirstName())
                .otherForenames(other.getOtherForenames())
                .lastName(other.getLastName())
                .dateOfBirth(other.getDateOfBirth())
                .ceasedOn(other.getCeasedOn())
                .registerEntryDate(other.getRegisterEntryDate());
        }

        public Builder title(final String value) {

            buildSteps.add(data -> data.title = value);
            return this;
        }

        public Builder firstName(final String value) {

            buildSteps.add(data -> data.firstName = value);
            return this;
        }

        public Builder otherForenames(final String value) {

            buildSteps.add(data -> data.otherForenames = value);
            return this;
        }

        public Builder lastName(final String value) {

            buildSteps.add(data -> data.lastName = value);
            return this;
        }

        public Builder dateOfBirth(final String value) {

            buildSteps.add(data -> data.dateOfBirth = value);
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

        public FilingDataDto build() {

            final var data = new FilingDataDto();
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilingDataDto that = (FilingDataDto) o;
        return Objects.equals(getTitle(), that.getTitle()) &&
            Objects.equals(getFirstName(), that.getFirstName()) &&
            Objects.equals(getOtherForenames(), that.getOtherForenames()) &&
            Objects.equals(getLastName(), that.getLastName()) &&
            Objects.equals(getDateOfBirth(), that.getDateOfBirth()) &&
            Objects.equals(getCeasedOn(), that.getCeasedOn()) &&
            Objects.equals(getRegisterEntryDate(), that.getRegisterEntryDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getFirstName(), getOtherForenames(), getLastName(), getDateOfBirth(),
            getCeasedOn(), getRegisterEntryDate());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("title", title)
            .append("firstName", firstName)
            .append("otherForenames", otherForenames)
            .append("lastName", lastName)
            .append("dateOfBirth", dateOfBirth)
            .append("ceasedOn", ceasedOn)
            .append("registerEntryDate", registerEntryDate)
            .toString();
    }
}
