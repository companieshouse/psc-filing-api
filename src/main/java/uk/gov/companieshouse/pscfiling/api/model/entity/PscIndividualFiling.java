package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "psc_filing")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PscIndividualFiling implements PscCommunal {

    private final PscCommunal pscCommunal;
    private String countryOfResidence;
    private Date3Tuple dateOfBirth;
    private NameElements nameElements;
    private String nationality;
    private Address residentialAddress;
    private Boolean residentialAddressSameAsCorrespondenceAddress;
    private LocalDate statementActionDate;
    private String statementType;

    private PscIndividualFiling(final PscCommon.Builder commonBuilder) {
        Objects.requireNonNull(commonBuilder);
        pscCommunal = commonBuilder.build();
    }

    @Override
    public String getId() {
        return pscCommunal.getId();
    }

    @Override
    public Address getAddress() {
        return pscCommunal.getAddress();
    }

    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return pscCommunal.getAddressSameAsRegisteredOfficeAddress();
    }

    @Override
    public LocalDate getCeasedOn() {
        return pscCommunal.getCeasedOn();
    }

    @Override
    public Instant getCreatedAt() {
        return pscCommunal.getCreatedAt();
    }

    @Override
    public String getEtag() {
        return pscCommunal.getEtag();
    }

    @Override
    public String getKind() {
        return pscCommunal.getKind();
    }

    @Override
    public Links getLinks() {
        return pscCommunal.getLinks();
    }

    @Override
    public List<String> getNaturesOfControl() {
        return pscCommunal.getNaturesOfControl();
    }

    @Override
    public LocalDate getNotifiedOn() {
        return pscCommunal.getNotifiedOn();
    }

    @Override
    public String getReferenceEtag() {
        return pscCommunal.getReferenceEtag();
    }

    @Override
    public String getReferencePscId() {
        return pscCommunal.getReferencePscId();
    }

    @Override
    public String getReferencePscListEtag() {
        return pscCommunal.getReferencePscListEtag();
    }

    @Override
    public LocalDate getRegisterEntryDate() {
        return pscCommunal.getRegisterEntryDate();
    }

    @Override
    public Instant getUpdatedAt() {
        return pscCommunal.getUpdatedAt();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PscIndividualFiling that = (PscIndividualFiling) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getAddress(), that.getAddress())
                && Objects.equals(getAddressSameAsRegisteredOfficeAddress(),
                that.getAddressSameAsRegisteredOfficeAddress())
                && Objects.equals(getCeasedOn(), that.getCeasedOn())
                && Objects.equals(getCountryOfResidence(), that.getCountryOfResidence())
                && Objects.equals(getCreatedAt(), that.getCreatedAt())
                && Objects.equals(getDateOfBirth(), that.getDateOfBirth())
                && Objects.equals(getEtag(), that.getEtag())
                && Objects.equals(getKind(), that.getKind())
                && Objects.equals(getNameElements(), that.getNameElements())
                && Objects.equals(getNaturesOfControl(), that.getNaturesOfControl())
                && Objects.equals(getNationality(), that.getNationality())
                && Objects.equals(getNotifiedOn(), that.getNotifiedOn())
                && Objects.equals(getReferenceEtag(), that.getReferenceEtag())
                && Objects.equals(getReferencePscId(), that.getReferencePscId())
                && Objects.equals(getReferencePscListEtag(), that.getReferencePscListEtag())
                && Objects.equals(getResidentialAddress(), that.getResidentialAddress())
                && Objects.equals(getResidentialAddressSameAsCorrespondenceAddress(),
                that.getResidentialAddressSameAsCorrespondenceAddress())
                && Objects.equals(getStatementActionDate(), that.getStatementActionDate())
                && Objects.equals(getStatementType(), that.getStatementType())
                && Objects.equals(getUpdatedAt(), that.getUpdatedAt())
                && Objects.equals(getLinks(), that.getLinks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAddress(), getAddressSameAsRegisteredOfficeAddress(),
                getCeasedOn(), getCountryOfResidence(), getCreatedAt(), getDateOfBirth(), getEtag(),
                getKind(), getNameElements(), getNaturesOfControl(), getNationality(),
                getNotifiedOn(), getReferenceEtag(), getReferencePscId(), getReferencePscListEtag(),
                getResidentialAddress(), getResidentialAddressSameAsCorrespondenceAddress(),
                getStatementActionDate(), getStatementType(), getUpdatedAt(), getLinks());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscIndividualFiling.class.getSimpleName() + "[", "]").add(
                        pscCommunal.toString())
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

    public static class Builder {

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
                    .countryOfResidence(other.getCountryOfResidence())
                    .createdAt(other.getCreatedAt())
                    .dateOfBirth(other.getDateOfBirth())
                    .etag(other.getEtag())
                    .kind(other.getKind())
                    .links(other.getLinks())
                    .nameElements(other.getNameElements())
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
                    .updatedAt(other.getUpdatedAt());
        }

        public Builder id(final String value) {

            commonBuilder.id(value);
            return this;
        }

        public Builder address(final Address value) {

            commonBuilder.address(value);
            return this;
        }

        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {

            commonBuilder.addressSameAsRegisteredOfficeAddress(value);
            return this;
        }

        public Builder ceasedOn(final LocalDate value) {

            commonBuilder.ceasedOn(value);
            return this;
        }

        public Builder countryOfResidence(final String value) {

            buildSteps.add(data -> data.countryOfResidence = value);
            return this;
        }

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

        public Builder etag(final String value) {

            commonBuilder.etag(value);
            return this;
        }

        public Builder kind(final String value) {

            commonBuilder.kind(value);
            return this;
        }

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

        public Builder naturesOfControl(final List<String> value) {

            commonBuilder.naturesOfControl(value);
            return this;
        }

        public Builder nationality(final String value) {

            buildSteps.add(data -> data.nationality = value);
            return this;
        }

        public Builder notifiedOn(final LocalDate value) {

            commonBuilder.notifiedOn(value);
            return this;
        }

        public Builder referenceEtag(final String value) {

            commonBuilder.referenceEtag(value);
            return this;
        }

        public Builder referencePscId(final String value) {

            commonBuilder.referencePscId(value);
            return this;
        }

        public Builder referencePscListEtag(final String value) {

            commonBuilder.referencePscListEtag(value);
            return this;
        }

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

        public Builder updatedAt(final Instant value) {

            commonBuilder.updatedAt(value);
            return this;
        }

        public PscIndividualFiling build() {
            final var data = new PscIndividualFiling(commonBuilder);
            buildSteps.forEach(s -> s.accept(data));

            return data;
        }
    }
}
