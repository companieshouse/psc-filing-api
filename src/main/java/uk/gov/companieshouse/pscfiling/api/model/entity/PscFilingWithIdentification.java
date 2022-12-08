package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

public class PscFilingWithIdentification implements PscCommunal {

    @Id
    private String id;
    @Unwrapped.Empty
    private final PscCommon pscCommon;
    private Identification identification;
    private String name;


    private PscFilingWithIdentification(final PscCommon.Builder commonBuilder) {
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

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PscFilingWithIdentification that = (PscFilingWithIdentification) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(pscCommon, that.pscCommon)
                && Objects.equals(getIdentification(), that.getIdentification())
                && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), pscCommon, getIdentification(), getName());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscFilingWithIdentification.class.getSimpleName() + "[",
                "]").add("id='" + id + "'")
                .add(pscCommon.toString())
                .add("identification=" + identification)
                .add("name='" + name + "'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<PscFilingWithIdentification>> buildSteps;
        private final PscCommon.Builder commonBuilder = PscCommon.builder();

        public Builder() {
            this.buildSteps = new ArrayList<>();
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

        public Builder updatedAt(final Instant value) {
            commonBuilder.updatedAt(value);
            return this;
        }

        public Builder identification(final Identification value) {
            buildSteps.add(data -> data.identification = Optional.ofNullable(value)
                    .map(v -> Identification.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder name(final String value) {
            buildSteps.add(data -> data.name = value);
            return this;
        }

        public PscFilingWithIdentification build() {

            final var data = new PscFilingWithIdentification(commonBuilder);
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }

    }
}
