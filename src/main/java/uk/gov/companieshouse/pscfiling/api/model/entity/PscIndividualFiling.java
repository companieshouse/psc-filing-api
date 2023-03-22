package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "psc_submissions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PscIndividualFiling extends PscCommon implements PscCommunal, Touchable {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    private String countryOfResidence;
    @JsonMerge
    private Date3Tuple dateOfBirth;
    @JsonMerge
    private NameElements nameElements;
    private String nationality;
    @JsonMerge
    private Address residentialAddress;
    private Boolean residentialAddressSameAsCorrespondenceAddress;
    private LocalDate statementActionDate;
    private String statementType;

    public PscIndividualFiling() {
        // required by Spring JPA
        PscCommon.builder()
                .build();
    }

    public String getId() {
        return id;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public Date3Tuple getDateOfBirth() {
        return dateOfBirth;
    }

    public NameElements getNameElements() {
        return nameElements;
    }

    public String getNationality() {
        return nationality;
    }

    public Address getResidentialAddress() {
        return residentialAddress;
    }

    public Boolean getResidentialAddressSameAsCorrespondenceAddress() {
        return residentialAddressSameAsCorrespondenceAddress;
    }

    public LocalDate getStatementActionDate() {
        return statementActionDate;
    }

    public String getStatementType() {
        return statementType;
    }

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
        final PscIndividualFiling that = (PscIndividualFiling) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getCountryOfResidence(), that.getCountryOfResidence())
                && Objects.equals(getDateOfBirth(), that.getDateOfBirth())
                && Objects.equals(getNameElements(), that.getNameElements())
                && Objects.equals(getNationality(), that.getNationality())
                && Objects.equals(getResidentialAddress(), that.getResidentialAddress())
                && Objects.equals(getResidentialAddressSameAsCorrespondenceAddress(),
                that.getResidentialAddressSameAsCorrespondenceAddress())
                && Objects.equals(getStatementActionDate(), that.getStatementActionDate())
                && Objects.equals(getStatementType(), that.getStatementType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getCountryOfResidence(), getDateOfBirth(),
                getNameElements(), getNationality(), getResidentialAddress(),
                getResidentialAddressSameAsCorrespondenceAddress(), getStatementActionDate(),
                getStatementType());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscIndividualFiling.class.getSimpleName() + "[", "]").add(
                        "id='" + id + "'")
                .add("address=" + getAddress())
                .add("addressSameAsRegisteredOfficeAddress="
                        + getAddressSameAsRegisteredOfficeAddress())
                .add("ceasedOn=" + getCeasedOn())
                .add("name='" + getName() + "'")
                .add("createdAt=" + getCreatedAt())
                .add("etag='" + getEtag() + "'")
                .add("kind='" + getKind() + "'")
                .add("links=" + getLinks())
                .add("naturesOfControl=" + getNaturesOfControl())
                .add("notifiedOn=" + getNotifiedOn())
                .add("referenceEtag='" + getReferenceEtag() + "'")
                .add("referencePscId='" + getReferencePscId() + "'")
                .add("registerEntryDate=" + getRegisterEntryDate())
                .add("updatedAt=" + getUpdatedAt())
                .add("countryOfResidence='" + countryOfResidence + "'")
                .add("dateOfBirth=" + dateOfBirth)
                .add("nameElements=" + nameElements)
                .add("nationality='" + nationality + "'")
                .add("residentialAddress=" + residentialAddress)
                .add("residentialAddressSameAsCorrespondenceAddress="
                        + residentialAddressSameAsCorrespondenceAddress)
                .add("statementActionDate=" + statementActionDate)
                .add("statementType='" + statementType + "'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscIndividualFiling other) {
        return new Builder(other);
    }

    public static class Builder extends PscCommon.Builder {
        private final List<Consumer<PscIndividualFiling>> buildSteps;
        private final PscCommon.Builder commonBuilder = PscCommon.builder();

        private Builder() {
            buildSteps = new ArrayList<>();
        }

        public Builder(final PscIndividualFiling other) {
            this();
            this.id(other.getId())
                    .address(other.getAddress())
                    .addressSameAsRegisteredOfficeAddress(
                            other.getAddressSameAsRegisteredOfficeAddress())
                    .ceasedOn(other.getCeasedOn())
                    .name(other.getName())
                    .countryOfResidence(other.getCountryOfResidence())
                    .createdAt(other.getCreatedAt())
                    .dateOfBirth(other.getDateOfBirth())
                    .registerEntryDate(other.getRegisterEntryDate())
                    .etag(other.getEtag())
                    .kind(other.getKind())
                    .links(other.getLinks())
                    .nameElements(other.getNameElements())
                    .naturesOfControl(other.getNaturesOfControl())
                    .nationality(other.getNationality())
                    .notifiedOn(other.getNotifiedOn())
                    .referenceEtag(other.getReferenceEtag())
                    .referencePscId(other.getReferencePscId())
                    .residentialAddress(other.getResidentialAddress())
                    .residentialAddressSameAsCorrespondenceAddress(
                            other.getResidentialAddressSameAsCorrespondenceAddress())
                    .statementActionDate(other.getStatementActionDate())
                    .statementType(other.getStatementType())
                    .updatedAt(other.getUpdatedAt());
        }

        public Builder id(final String value) {

            buildSteps.add(data -> data.id = value);
            return this;
        }

        @Override
        public Builder address(final Address value) {

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
        public Builder name(final String value) {

            commonBuilder.name(value);
            return this;
        }

        public Builder countryOfResidence(final String value) {

            buildSteps.add(data -> data.countryOfResidence = value);
            return this;
        }

        @Override
        public Builder createdAt(final Instant value) {

            commonBuilder.createdAt(value);
            return this;
        }

        public Builder dateOfBirth(final Date3Tuple value) {

            buildSteps.add(data -> data.dateOfBirth = Optional.ofNullable(value)
                    .map(v -> new Date3Tuple(v.getDay(), v.getMonth(), v.getYear()))
                    .orElse(null));
            return this;
        }

        @Override
        public Builder etag(final String value) {

            commonBuilder.etag(value);
            return this;
        }

        @Override
        public Builder kind(final String value) {

            commonBuilder.kind(value);
            return this;
        }

        @Override
        public Builder links(final Links value) {

            commonBuilder.links(value);
            return this;
        }

        public Builder nameElements(final NameElements value) {

            buildSteps.add(data -> data.nameElements = Optional.ofNullable(value)
                    .map(v -> new NameElements(v.getForename(), v.getOtherForenames(),
                            v.getSurname(), v.getTitle()))
                    .orElse(null));
            return this;
        }

        @Override
        public Builder naturesOfControl(final List<String> value) {

            commonBuilder.naturesOfControl(value);
            return this;
        }

        public Builder nationality(final String value) {

            buildSteps.add(data -> data.nationality = value);
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

        public Builder residentialAddress(final Address value) {

            buildSteps.add(data -> data.residentialAddress = Optional.ofNullable(value)
                    .map(v -> Address.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder residentialAddressSameAsCorrespondenceAddress(final Boolean value) {

            buildSteps.add(data -> data.residentialAddressSameAsCorrespondenceAddress = value);
            return this;
        }

        public Builder statementActionDate(final LocalDate value) {

            buildSteps.add(data -> data.statementActionDate = value);
            return this;
        }

        public Builder statementType(final String value) {

            buildSteps.add(data -> data.statementType = value);
            return this;
        }

        @Override
        public Builder updatedAt(final Instant value) {

            commonBuilder.updatedAt(value);
            return this;
        }

        @Override
        public PscIndividualFiling build() {
            final var data = new PscIndividualFiling();
            commonBuilder.commonBuildSteps.forEach(s -> s.accept(data));
            buildSteps.forEach(s -> s.accept(data));

            return data;
        }
    }
}