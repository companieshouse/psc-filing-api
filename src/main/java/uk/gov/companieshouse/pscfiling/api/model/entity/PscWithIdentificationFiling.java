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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

@Document(collection = "psc_submissions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PscWithIdentificationFiling implements PscFiling {

    @Id
    private String id;
    @Unwrapped.Empty
    private PscCommon pscCommon;
    private Identification identification;
    private LocalDate statementActionDate;
    private String statementType;

    public PscWithIdentificationFiling() {
        // required by Spring JPA
        pscCommon = PscCommon.builder()
                .build();
    }

    private PscWithIdentificationFiling(final PscCommon.Builder commonBuilder) {
        Objects.requireNonNull(commonBuilder);
        pscCommon = commonBuilder.build();
    }

    public String getId() {
        return id;
    }

    @Override
    public Address getAddress() {
        return pscCommon.getAddress();
    }

    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return pscCommon.getAddressSameAsRegisteredOfficeAddress();
    }

    @Override
    public LocalDate getCeasedOn() {
        return pscCommon.getCeasedOn();
    }

    @Override
    public String getName() {
        return pscCommon.getName();
    }

    @Override
    public Instant getCreatedAt() {
        return pscCommon.getCreatedAt();
    }

    @Override
    public String getEtag() {
        return pscCommon.getEtag();
    }

    @Override
    public String getKind() {
        return pscCommon.getKind();
    }

    @Override
    public Links getLinks() {
        return pscCommon.getLinks();
    }

    @Override
    public List<String> getNaturesOfControl() {
        return pscCommon.getNaturesOfControl();
    }

    @Override
    public LocalDate getNotifiedOn() {
        return pscCommon.getNotifiedOn();
    }

    @Override
    public String getReferenceEtag() {
        return pscCommon.getReferenceEtag();
    }

    @Override
    public String getReferencePscId() {
        return pscCommon.getReferencePscId();
    }

    @Override
    public String getReferencePscListEtag() {
        return pscCommon.getReferencePscListEtag();
    }

    @Override
    public LocalDate getRegisterEntryDate() {
        return pscCommon.getRegisterEntryDate();
    }

    @Override
    public Instant getUpdatedAt() {
        return pscCommon.getUpdatedAt();
    }

    public Identification getIdentification() {
        return identification;
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
        final PscWithIdentificationFiling that = (PscWithIdentificationFiling) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(pscCommon, that.pscCommon)
                && Objects.equals(getIdentification(), that.getIdentification())
                && Objects.equals(getStatementActionDate(), that.getStatementActionDate())
                && Objects.equals(getStatementType(), that.getStatementType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), pscCommon, getIdentification(),
                getStatementActionDate(), getStatementType());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscWithIdentificationFiling.class.getSimpleName() + "[", "]").add(
                        "id='" + id + "'")
                .add(pscCommon.toString())
                .add("identification='" + identification + "'")
                .add("statementActionDate=" + statementActionDate)
                .add("statementType='" + statementType + "'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscWithIdentificationFiling other) {
        return new Builder(other);
    }

    public static class Builder {

        private final List<Consumer<PscWithIdentificationFiling>> buildSteps;
        private final PscCommon.Builder commonBuilder = PscCommon.builder();

        private Builder() {
            buildSteps = new ArrayList<>();
        }

        public Builder(final PscWithIdentificationFiling other) {
            this();
            this.id(other.getId())
                    .address(other.getAddress())
                    .addressSameAsRegisteredOfficeAddress(
                            other.getAddressSameAsRegisteredOfficeAddress())
                    .ceasedOn(other.getCeasedOn())
                    .name(other.getName())
                    .identification(other.getIdentification())
                    .createdAt(other.getCreatedAt())
                    .registerEntryDate(other.getRegisterEntryDate())
                    .etag(other.getEtag())
                    .kind(other.getKind())
                    .links(other.getLinks())
                    .naturesOfControl(other.getNaturesOfControl())
                    .notifiedOn(other.getNotifiedOn())
                    .referenceEtag(other.getReferenceEtag())
                    .referencePscId(other.getReferencePscId())
                    .referencePscListEtag(other.getReferencePscListEtag())
                    .statementActionDate(other.getStatementActionDate())
                    .statementType(other.getStatementType())
                    .updatedAt(other.getUpdatedAt());
        }

        public Builder id(final String value) {

            buildSteps.add(data -> data.id = value);
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

        public Builder name(final String value) {

            commonBuilder.name(value);
            return this;
        }

        public Builder identification(final Identification value) {

            buildSteps.add(data -> data.identification = value);
            return this;
        }

        public Builder createdAt(final Instant value) {

            commonBuilder.createdAt(value);
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

        public Builder naturesOfControl(final List<String> value) {

            commonBuilder.naturesOfControl(value);
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

        public PscWithIdentificationFiling build() {
            final var data = new PscWithIdentificationFiling(commonBuilder);
            buildSteps.forEach(s -> s.accept(data));

            return data;
        }
    }
}