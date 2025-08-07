package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The Individual filing data dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualFilingDataDto implements FilingDtoCommunal {

    private String title;
    private String firstName;
    private String otherForenames;
    private String lastName;
    private String dateOfBirth;
    private String ceasedOn;
    private String registerEntryDate;

    public IndividualFilingDataDto() {
        // prevent direct instantiation
    }

    /**
     * @return the individual's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the individual's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the individual's other forenames
     */
    public String getOtherForenames() {
        return otherForenames;
    }

    /**
     * @return the individual's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the individual's date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @return the date the individual ceased on
     */
    public String getCeasedOn() {
        return ceasedOn;
    }

    /**
     * @return the date the entry was registered on
     */
    public String getRegisterEntryDate() {
        return registerEntryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final IndividualFilingDataDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<IndividualFilingDataDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final IndividualFilingDataDto other) {
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

        public IndividualFilingDataDto build() {

            final var data = new IndividualFilingDataDto();
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
        IndividualFilingDataDto that = (IndividualFilingDataDto) o;
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
