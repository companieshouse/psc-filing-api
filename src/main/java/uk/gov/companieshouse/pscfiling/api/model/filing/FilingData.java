package uk.gov.companieshouse.pscfiling.api.model.filing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilingData {

    private String firstName;
    private String otherForenames;
    private String lastName;
    private String dateOfBirth;
    private String ceasedOn;
    private String registerEntryDate;

    @JsonCreator
    public FilingData(@JsonProperty("first_name") String firstName,
                      @JsonProperty("other_forenames") String otherForenames,
                      @JsonProperty("last_name") String lastName,
                      @JsonProperty("date_of_birth") String dateOfBirth,
                      @JsonProperty("ceased_on") String ceasedOn,
                      @JsonProperty("register_entry_date") String registerEntryDate) {
        this.firstName = firstName;
        this.otherForenames = otherForenames;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.ceasedOn = ceasedOn;
        this.registerEntryDate = registerEntryDate;
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
}
