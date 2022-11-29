package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

@JsonDeserialize(builder = NameElementsDto.Builder.class)
public class NameElementsDto {

    private String forename;
    private String otherForenames;
    private String surname;
    private String title;

    private NameElementsDto() {
        // prevent direct instantiation
    }

    @JsonCreator
    public NameElementsDto(@JsonProperty("forename") final String forename,
                           @JsonProperty("other_forenames") final String otherForenames,
                           @JsonProperty("surname") final String surname,
                           @JsonProperty("title") final String title) {
        this.forename = forename;
        this.otherForenames = otherForenames;
        this.surname = surname;
        this.title = title;
    }

    public String getForename() {
        return forename;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public String getSurname() {
        return surname;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NameElementsDto that = (NameElementsDto) o;
        return Objects.equals(getForename(), that.getForename()) &&
                Objects.equals(getOtherForenames(), that.getOtherForenames()) &&
                Objects.equals(getSurname(), that.getSurname()) &&
                Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getForename(), getOtherForenames(), getSurname(), getTitle());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NameElementsDto.class.getSimpleName() + "[", "]").add(
                        "forename='" + forename + "'").add("otherForenames='" + otherForenames + "'")
                .add("surname='" + surname + "'").add("title='" + title + "'").toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final NameElementsDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<NameElementsDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final NameElementsDto other) {
            this();
            this.forename(other.getForename())
                    .otherForenames(other.getOtherForenames())
                    .surname(other.getSurname())
                    .title(other.getTitle());
        }

        public Builder forename(final String value) {

            buildSteps.add(data -> data.forename = value);
            return this;
        }

        public Builder otherForenames(final String value) {

            buildSteps.add(data -> data.otherForenames = value);
            return this;
        }

        public Builder surname(final String value) {

            buildSteps.add(data -> data.surname = value);
            return this;
        }

        public Builder title(final String value) {

            buildSteps.add(data -> data.title = value);
            return this;
        }

        public NameElementsDto build() {

            final var data = new NameElementsDto();
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }
    }
}
