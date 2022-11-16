package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NamesElement {

    String forename;
    String otherForenames;
    String surname;
    String title;

    public NamesElement(String forename, String otherForenames, String surname, String title) {
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
        NamesElement that = (NamesElement) o;
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
        return new StringJoiner(", ", NamesElement.class.getSimpleName() + "[", "]").add("forename='" + forename + "'")
                .add("otherForenames='" + otherForenames + "'").add("surname='" + surname + "'")
                .add("title='" + title + "'").toString();
    }
}
