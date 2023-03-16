package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class PscCommon implements PscCommunal, Touchable {
    @JsonMerge
    private Address address;
    private Boolean addressSameAsRegisteredOfficeAddress;
    private LocalDate ceasedOn;
    private String name;
    private Instant createdAt;
    private String etag;
    private String kind;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Links links;
    @JsonMerge(OptBoolean.FALSE)
    private NaturesOfControlList naturesOfControl;
    private LocalDate notifiedOn;
    private String referenceEtag;
    private String referencePscId;
    private String referencePscListEtag;
    private LocalDate registerEntryDate;
    private Instant updatedAt;

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return addressSameAsRegisteredOfficeAddress;
    }

    @Override
    public LocalDate getCeasedOn() {
        return ceasedOn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getEtag() {
        return etag;
    }

    @Override
    public String getKind() {
        return kind;
    }

    @Override
    public Links getLinks() {
        return links;
    }

    @Override
    public List<String> getNaturesOfControl() {
        return naturesOfControl;
    }

    @Override
    public LocalDate getNotifiedOn() {
        return notifiedOn;
    }

    @Override
    public String getReferenceEtag() {
        return referenceEtag;
    }

    @Override
    public String getReferencePscId() {
        return referencePscId;
    }

    @Override
    public String getReferencePscListEtag() {
        return referencePscListEtag;
    }

    @Override
    public LocalDate getRegisterEntryDate() {
        return registerEntryDate;
    }

    @Override
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void touch(final Instant instant) {
        this.updatedAt = instant;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var pscCommon = (PscCommon) o;
        return Objects.equals(getAddress(), pscCommon.getAddress())
                && Objects.equals(getAddressSameAsRegisteredOfficeAddress(),
                pscCommon.getAddressSameAsRegisteredOfficeAddress())
                && Objects.equals(getCeasedOn(), pscCommon.getCeasedOn())
                && Objects.equals(getName(), pscCommon.getName())
                && Objects.equals(getCreatedAt(), pscCommon.getCreatedAt())
                && Objects.equals(getEtag(), pscCommon.getEtag())
                && Objects.equals(getKind(), pscCommon.getKind())
                && Objects.equals(getLinks(), pscCommon.getLinks())
                && Objects.equals(getNaturesOfControl(), pscCommon.getNaturesOfControl())
                && Objects.equals(getNotifiedOn(), pscCommon.getNotifiedOn())
                && Objects.equals(getReferenceEtag(), pscCommon.getReferenceEtag())
                && Objects.equals(getReferencePscId(), pscCommon.getReferencePscId())
                && Objects.equals(getReferencePscListEtag(), pscCommon.getReferencePscListEtag())
                && Objects.equals(getRegisterEntryDate(), pscCommon.getRegisterEntryDate())
                && Objects.equals(getUpdatedAt(), pscCommon.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress(), getAddressSameAsRegisteredOfficeAddress(), getCeasedOn(),
                getName(), getCreatedAt(), getEtag(), getKind(), getLinks(), getNaturesOfControl(),
                getNotifiedOn(), getReferenceEtag(), getReferencePscId(), getReferencePscListEtag(),
                getRegisterEntryDate(), getUpdatedAt());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ")
                .add("address=" + address)
                .add("addressSameAsRegisteredOfficeAddress=" + addressSameAsRegisteredOfficeAddress)
                .add("name=" + name)
                .add("ceasedOn=" + ceasedOn)
                .add("createdAt=" + createdAt)
                .add("etag='" + etag + "'")
                .add("kind='" + kind + "'")
                .add("links=" + links)
                .add("naturesOfControl=" + naturesOfControl)
                .add("notifiedOn=" + notifiedOn)
                .add("referenceEtag='" + referenceEtag + "'")
                .add("referencePscId='" + referencePscId + "'")
                .add("referencePscListEtag='" + referencePscListEtag + "'")
                .add("registerEntryDate=" + registerEntryDate)
                .add("updatedAt=" + updatedAt)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscCommon other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        protected final List<Consumer<PscCommon>> commonBuildSteps;

        public Builder() {
            this.commonBuildSteps = new ArrayList<>();
        }

        public Builder(final PscCommon other) {
            this();
            this.address(other.getAddress())
                    .addressSameAsRegisteredOfficeAddress(
                            other.getAddressSameAsRegisteredOfficeAddress())
                    .name(other.getName())
                    .ceasedOn(other.getCeasedOn())
                    .createdAt(other.createdAt)
                    .etag(other.getEtag())
                    .kind(other.getKind())
                    .links(other.getLinks())
                    .naturesOfControl(other.getNaturesOfControl())
                    .notifiedOn(other.getNotifiedOn())
                    .referenceEtag(other.getReferenceEtag())
                    .referencePscId(other.getReferencePscId())
                    .referencePscListEtag(other.getReferencePscListEtag())
                    .registerEntryDate(other.getRegisterEntryDate())
                    .updatedAt(other.getUpdatedAt());
        }

        public Builder address(final Address value) {

            commonBuildSteps.add(data -> data.address = Optional.ofNullable(value)
                    .map(v -> Address.builder(v)
                            .build())
                    .orElse(null));
            return this;
        }

        public Builder addressSameAsRegisteredOfficeAddress(final Boolean value) {

            commonBuildSteps.add(data -> data.addressSameAsRegisteredOfficeAddress = value);
            return this;
        }

        public Builder ceasedOn(final LocalDate value) {

            commonBuildSteps.add(data -> data.ceasedOn = value);
            return this;
        }

        public Builder name(final String value) {

            commonBuildSteps.add(data -> data.name = value);
            return this;
        }

        public Builder createdAt(final Instant value) {

            commonBuildSteps.add(data -> data.createdAt = value);
            return this;
        }

        public Builder etag(final String value) {

            commonBuildSteps.add(data -> data.etag = value);
            return this;
        }

        public Builder kind(final String value) {

            commonBuildSteps.add(data -> data.kind = value);
            return this;
        }

        public Builder links(final Links value) {

            commonBuildSteps.add(data -> data.links = Optional.ofNullable(value)
                    .map(v -> new Links(v.getSelf(), v.getValidationStatus()))
                    .orElse(null));
            return this;
        }

        public Builder naturesOfControl(final List<String> value) {
            commonBuildSteps.add(data -> data.naturesOfControl =
                    Optional.ofNullable(value).map(NaturesOfControlList::new).orElse(null));
            return this;
        }

        public Builder notifiedOn(final LocalDate value) {

            commonBuildSteps.add(data -> data.notifiedOn = value);
            return this;
        }

        public Builder referenceEtag(final String value) {

            commonBuildSteps.add(data -> data.referenceEtag = value);
            return this;
        }

        public Builder referencePscId(final String value) {

            commonBuildSteps.add(data -> data.referencePscId = value);
            return this;
        }

        public Builder referencePscListEtag(final String value) {

            commonBuildSteps.add(data -> data.referencePscListEtag = value);
            return this;
        }

        public Builder registerEntryDate(final LocalDate value) {

            commonBuildSteps.add(data -> data.registerEntryDate = value);
            return this;
        }

        public Builder updatedAt(final Instant value) {

            commonBuildSteps.add(data -> data.updatedAt = value);
            return this;
        }

        public PscCommon build() {

            final var data = new PscCommon();
            commonBuildSteps.forEach(step -> step.accept(data));

            return data;
        }

    }
}
