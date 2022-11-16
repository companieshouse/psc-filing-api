package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "officer_filing")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PscIndividualFiling {

    @Id
    private String id;
    private Address address;
    private Boolean addressSameAsRegisteredOfficeAddress;
    private LocalDate ceasedOn;
    private String countryOfResidence;
    private Instant createdAt;
    private Date3Tuple dateOfBirth;
    private String etag;
    private Identification identification;
    private String kind;
    private String name;
    private NamesElement namesElement;
    private Set<String> naturesOfControl;
    private String nationality;
    private LocalDate notifiedOn;
    private String referenceEtag;
    private String referencePscId;
    private String referencePscListEtag;
    private Address residentialAddress;
    private Boolean residentialAddressSameAsCorrespondenceAddress;
    private LocalDate statementActionDate;
    private String statementType;
    private Instant updatedAt;
    private Links links;

    private PscIndividualFiling() {
    }

    public String getId() {
        return id;
    }

    public Address getAddress() {
        return address;
    }

    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return addressSameAsRegisteredOfficeAddress;
    }

    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Date3Tuple getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEtag() {
        return etag;
    }

    public Identification getIdentification() {
        return identification;
    }

    public String getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public NamesElement getNamesElement() {
        return namesElement;
    }

    public Set<String> getNaturesOfControl() {
        return naturesOfControl;
    }

    public String getNationality() {
        return nationality;
    }

    public LocalDate getNotifiedOn() {
        return notifiedOn;
    }

    public String getReferenceEtag() {
        return referenceEtag;
    }

    public String getReferencePscId() {
        return referencePscId;
    }

    public String getReferencePscListEtag() {
        return referencePscListEtag;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Links getLinks() {
        return links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PscIndividualFiling that = (PscIndividualFiling) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getAddressSameAsRegisteredOfficeAddress(),
                        that.getAddressSameAsRegisteredOfficeAddress()) &&
                Objects.equals(getCeasedOn(), that.getCeasedOn()) &&
                Objects.equals(getCountryOfResidence(), that.getCountryOfResidence()) &&
                Objects.equals(getCreatedAt(), that.getCreatedAt()) &&
                Objects.equals(getDateOfBirth(), that.getDateOfBirth()) &&
                Objects.equals(getEtag(), that.getEtag()) &&
                Objects.equals(getIdentification(), that.getIdentification()) &&
                Objects.equals(getKind(), that.getKind()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getNamesElement(), that.getNamesElement()) &&
                Objects.equals(getNaturesOfControl(), that.getNaturesOfControl()) &&
                Objects.equals(getNationality(), that.getNationality()) &&
                Objects.equals(getNotifiedOn(), that.getNotifiedOn()) &&
                Objects.equals(getReferenceEtag(), that.getReferenceEtag()) &&
                Objects.equals(getReferencePscId(), that.getReferencePscId()) &&
                Objects.equals(getReferencePscListEtag(), that.getReferencePscListEtag()) &&
                Objects.equals(getResidentialAddress(), that.getResidentialAddress()) &&
                Objects.equals(getResidentialAddressSameAsCorrespondenceAddress(),
                        that.getResidentialAddressSameAsCorrespondenceAddress()) &&
                Objects.equals(getStatementActionDate(), that.getStatementActionDate()) &&
                Objects.equals(getStatementType(), that.getStatementType()) &&
                Objects.equals(getUpdatedAt(), that.getUpdatedAt()) &&
                Objects.equals(getLinks(), that.getLinks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAddress(), getAddressSameAsRegisteredOfficeAddress(), getCeasedOn(),
                getCountryOfResidence(), getCreatedAt(), getDateOfBirth(), getEtag(), getIdentification(), getKind(),
                getName(), getNamesElement(), getNaturesOfControl(), getNationality(), getNotifiedOn(),
                getReferenceEtag(), getReferencePscId(), getReferencePscListEtag(), getResidentialAddress(),
                getResidentialAddressSameAsCorrespondenceAddress(), getStatementActionDate(), getStatementType(),
                getUpdatedAt(), getLinks());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscIndividualFiling.class.getSimpleName() + "[", "]").add("id='" + id + "'")
                .add("address=" + address)
                .add("addressSameAsRegisteredOfficeAddress=" + addressSameAsRegisteredOfficeAddress)
                .add("ceasedOn=" + ceasedOn).add("countryOfResidence='" + countryOfResidence + "'")
                .add("createdAt=" + createdAt).add("dateOfBirth=" + dateOfBirth).add("etag='" + etag + "'")
                .add("identification=" + identification).add("kind='" + kind + "'").add("name='" + name + "'")
                .add("namesElement=" + namesElement).add("naturesOfControl=" + naturesOfControl)
                .add("nationality='" + nationality + "'").add("notifiedOn=" + notifiedOn)
                .add("referenceEtag='" + referenceEtag + "'").add("referencePscId='" + referencePscId + "'")
                .add("referencePscListEtag='" + referencePscListEtag + "'")
                .add("residentialAddress=" + residentialAddress)
                .add("residentialAddressSameAsCorrespondenceAddress=" + residentialAddressSameAsCorrespondenceAddress)
                .add("statementActionDate=" + statementActionDate).add("statementType='" + statementType + "'")
                .add("updatedAt=" + updatedAt).add("links=" + links).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscIndividualFiling other) {
        return new Builder(other);
    }

    public static class Builder {

        private final List<Consumer<PscIndividualFiling>> buildSteps;

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
                    .countryOfResidence(other.getCountryOfResidence())
                    .createdAt(other.getCreatedAt())
                    .dateOfBirth(other.getDateOfBirth())
                    .etag(other.getEtag())
                    .identification(other.getIdentification())
                    .kind(other.getKind())
                    .name(other.getName())
                    .namesElement(other.getNamesElement())
                    .naturesOfControl(other.getNaturesOfControl())
                    .nationality(other.getNationality())
                    .notifiedOn(other.getNotifiedOn())
                    .referenceEtag(other.getReferenceEtag())
                    .referencePscId(other.getReferencePscId())
                    .referencePscListEtag(other.getReferencePscListEtag())
                    .residentialAddress(other.getResidentialAddress())
                    .residentialAddressSameAsCorrespondenceAddress(
            other.getResidentialAddressSameAsCorrespondenceAddress())
                    .statementActionDate(other.getStatementActionDate())
                    .statementType(other.getStatementType())
                    .updatedAt(other.getUpdatedAt())
                    .links(other.getLinks());
        }

        public Builder id(final String value) {
            buildSteps.add(data -> data.id = value);
            return this;
        }

        public Builder address(final Address value) {

            buildSteps.add(data -> data.address = Optional.ofNullable(value)
                    .map(v -> Address.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {

            buildSteps.add(data -> data.addressSameAsRegisteredOfficeAddress = value);
            return this;
        }

        public Builder ceasedOn(final LocalDate value) {

            buildSteps.add(data -> data.ceasedOn = value);
            return this;
        }

        public Builder countryOfResidence(final String value) {

            buildSteps.add(data -> data.countryOfResidence = value);
            return this;
        }

        public Builder createdAt(final Instant value) {

            buildSteps.add(data -> data.createdAt = value);
            return this;
        }

        public Builder dateOfBirth(final Date3Tuple value) {

            buildSteps.add(data -> data.dateOfBirth = Optional.ofNullable(value)
                    .map(v -> new Date3Tuple(v.getDay(), v.getMonth(), v.getYear()))
                    .orElse(null));
            return this;
        }

        public Builder etag(final String value) {

            buildSteps.add(data -> data.etag = value);
            return this;
        }

        public Builder identification(final Identification value) {

            buildSteps.add(data -> data.identification = Optional.ofNullable(value)
                    .map(v -> new Identification(v.getIdentificationType(),
                            v.getLegalAuthority(),
                            v.getLegalForm(),
                            v.getPlaceRegistered(),
                            v.getRegistrationNumber()))
                    .orElse(null));
            return this;
        }

        public Builder kind(final String value) {

            buildSteps.add(data -> data.kind = value);
            return this;
        }

        public Builder name(final String value) {

            buildSteps.add(data -> data.name = value);
            return this;
        }

        public Builder namesElement(final NamesElement value) {

            buildSteps.add(data -> data.namesElement = Optional.ofNullable(value)
                    .map(v -> new NamesElement(v.getForename(),
                            v.getOtherForenames(),
                            v.getSurname(),
                            v.getTitle()))
                    .orElse(null));
            return this;
        }

        public Builder naturesOfControl(final Set<String> value) {

            buildSteps.add(data -> data.naturesOfControl =
                    Optional.ofNullable(value).map(s -> new HashSet<>(s)).orElse(null));
            return this;
        }

        public Builder nationality(final String value) {

            buildSteps.add(data -> data.nationality = value);
            return this;
        }

        public Builder notifiedOn(final LocalDate value) {

            buildSteps.add(data -> data.notifiedOn = value);
            return this;
        }

        public Builder referenceEtag(final String value) {

            buildSteps.add(data -> data.referenceEtag = value);
            return this;
        }

        public Builder referencePscId(final String value) {

            buildSteps.add(data -> data.referencePscId = value);
            return this;
        }

        public Builder referencePscListEtag(final String value) {

            buildSteps.add(data -> data.referencePscListEtag = value);
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

        public Builder updatedAt(final Instant value) {

            buildSteps.add(data -> data.updatedAt = value);
            return this;
        }

        public Builder links(final Links value) {

            buildSteps.add(data -> data.links = Optional.ofNullable(value)
                    .map(v -> new Links(v.getSelf(), v.getValidationStatus()))
                    .orElse(null));
            return this;
        }

        public PscIndividualFiling build() {
            final var PscIndividualFiling = new PscIndividualFiling();
            buildSteps.forEach(s -> s.accept(PscIndividualFiling));

            return PscIndividualFiling;
        }
    }
}
