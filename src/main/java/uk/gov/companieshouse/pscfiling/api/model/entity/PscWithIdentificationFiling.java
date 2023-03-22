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
public class PscWithIdentificationFiling extends PscCommon implements PscCommunal, Touchable {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    @JsonMerge
    private Identification identification;
    private LocalDate statementActionDate;
    private String statementType;

    public PscWithIdentificationFiling() {
        // required by Spring JPA
        PscCommon.builder()
                .build();
    }

    public String getId() {
        return id;
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
        if (!super.equals(o)) {
            return false;
        }
        final PscWithIdentificationFiling that = (PscWithIdentificationFiling) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getIdentification(),
                that.getIdentification()) && Objects.equals(getStatementActionDate(),
                that.getStatementActionDate()) && Objects.equals(getStatementType(),
                that.getStatementType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getIdentification(),
                getStatementActionDate(), getStatementType());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscWithIdentificationFiling.class.getSimpleName() + "[", "]").add(
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

    public static class Builder extends PscCommon.Builder {

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

        public Builder identification(final Identification value) {
            buildSteps.add(data -> data.identification = Optional.ofNullable(value)
                    .map(v -> new Identification(v.getCountryRegistered(), v.getLegalAuthority(),
                            v.getLegalForm(), v.getPlaceRegistered(), v.getRegistrationNumber()))
                    .orElse(null));
            return this;
        }

        @Override
        public Builder createdAt(final Instant value) {

            commonBuilder.createdAt(value);
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

        @Override
        public Builder naturesOfControl(final List<String> value) {

            commonBuilder.naturesOfControl(value);
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
        public PscWithIdentificationFiling build() {
            final var data = new PscWithIdentificationFiling();
            commonBuilder.commonBuildSteps.forEach(s -> s.accept(data));
            buildSteps.forEach(s -> s.accept(data));

            return data;
        }
    }
}