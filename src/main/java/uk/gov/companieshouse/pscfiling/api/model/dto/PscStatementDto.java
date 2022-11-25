package uk.gov.companieshouse.pscfiling.api.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

@JsonDeserialize(builder = PscStatementDto.Builder.class)
public class PscStatementDto {

    private String referenceEtag;
    private String referenceStatementId;
    private String restrictionsNoticeWithdrawalReason;
    private LocalDate statementActionDate;
    private String statementType;
    private PscStatementDto() {
        // prevent direct instantiation
    }

    public String getReferenceEtag() {
        return referenceEtag;
    }

    public String getReferenceStatementId() {
        return referenceStatementId;
    }

    public String getRestrictionsNoticeWithdrawalReason() {
        return restrictionsNoticeWithdrawalReason;
    }

    public LocalDate getStatementActionDate() {
        return statementActionDate;
    }

    public String getStatementType() {
        return statementType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PscStatementDto other) {
        return new Builder(other);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private final List<Consumer<PscStatementDto>> buildSteps;

        public Builder() {
            this.buildSteps = new ArrayList<>();
        }

        public Builder(final PscStatementDto other) {
            this();
            this.referenceEtag(other.getReferenceEtag())
                    .referenceStatementId(other.getReferenceStatementId())
                    .restrictionsNoticeWithdrawalReason(other.getRestrictionsNoticeWithdrawalReason())
                    .statementActionDate(other.getStatementActionDate())
                    .statementType(other.getStatementType());
        }

        public Builder referenceEtag(final String value) {

            buildSteps.add(data -> data.referenceEtag = value);
            return this;
        }

        public Builder referenceStatementId(final String value) {

            buildSteps.add(data -> data.referenceStatementId = value);
            return this;
        }

        public Builder restrictionsNoticeWithdrawalReason(final String value) {

            buildSteps.add(data -> data.restrictionsNoticeWithdrawalReason = value);
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

        public PscStatementDto build() {

            final var data = new PscStatementDto();
            buildSteps.forEach(step -> step.accept(data));

            return data;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PscStatementDto that = (PscStatementDto) o;
        return Objects.equals(getReferenceEtag(), that.getReferenceEtag()) && Objects.equals(
                getReferenceStatementId(), that.getReferenceStatementId()) && Objects.equals(
                getRestrictionsNoticeWithdrawalReason(),
                that.getRestrictionsNoticeWithdrawalReason()) && Objects.equals(
                getStatementActionDate(), that.getStatementActionDate()) && Objects.equals(
                getStatementType(), that.getStatementType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReferenceEtag(), getReferenceStatementId(),
                getRestrictionsNoticeWithdrawalReason(), getStatementActionDate(),
                getStatementType());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PscStatementDto.class.getSimpleName() + "[", "]").add(
                        "referenceEtag='" + referenceEtag + "'")
                .add("referenceStatementId='" + referenceStatementId + "'")
                .add("restrictionsNoticeWithdrawalReason='"
                        + restrictionsNoticeWithdrawalReason
                        + "'")
                .add("statementActionDate=" + statementActionDate)
                .add("statementType='" + statementType + "'")
                .toString();
    }
}
