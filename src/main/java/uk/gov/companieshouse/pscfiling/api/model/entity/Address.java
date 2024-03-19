package uk.gov.companieshouse.pscfiling.api.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * The Address entity model.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {

    @NotNull
    @Field("address_line_1")
    private String addressLine1;
    @Field("address_line_2")
    private String addressLine2;
    private String careOf;
    private String country;
    private String locality;
    private String poBox;
    private String postalCode;
    private String premises;
    private String region;

    private Address() {
    }

    /**
     * @return First line of the address.
     */
    public String getAddressLine1() {
        return addressLine1;
    }

    /**
     * @return Second line of the address.
     */
    public String getAddressLine2() {
        return addressLine2;
    }

    /**
     * @return The care of.
     */
    public String getCareOf() {
        return careOf;
    }

    /**
     * @return The address county.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return The address locality.
     */
    public String getLocality() {
        return locality;
    }

    /**
     * @return The address po box.
     */
    public String getPoBox() {
        return poBox;
    }

    /**
     * @return The address postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return The address premises.
     */
    public String getPremises() {
        return premises;
    }

    /**
     * @return The address region.
     */
    public String getRegion() {
        return region;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var address = (Address) o;
        return getAddressLine1().equals(address.getAddressLine1())
                && Objects.equals(getAddressLine2(), address.getAddressLine2())
                && Objects.equals(getCareOf(), address.getCareOf())
                && getCountry().equals(address.getCountry())
                && Objects.equals(getLocality(), address.getLocality())
                && Objects.equals(getPoBox(), address.getPoBox())
                && getPostalCode().equals(address.getPostalCode())
                && Objects.equals(getPremises(), address.getPremises())
                && Objects.equals(getRegion(), address.getRegion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddressLine1(), getAddressLine2(), getCareOf(), getCountry(),
                getLocality(), getPoBox(), getPostalCode(), getPremises(), getRegion());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Address.class.getSimpleName() + "[", "]").add(
                        "addressLine1='" + addressLine1 + "'")
                .add("addressLine2='" + addressLine2 + "'")
                .add("careOf='" + careOf + "'")
                .add("country='" + country + "'")
                .add("locality='" + locality + "'")
                .add("poBox='" + poBox + "'")
                .add("postalCode='" + postalCode + "'")
                .add("premises='" + premises + "'")
                .add("region='" + region + "'")
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final Address other) {
        return new Builder(other);
    }

    public static class Builder {

        List<Consumer<Address>> buildSteps;

        private Builder() {

            buildSteps = new ArrayList<>();

        }

        public Builder(final Address other) {
            this();
            this.addressLine1(other.getAddressLine1())
                    .addressLine2(other.getAddressLine2())
                    .careOf(other.getCareOf())
                    .country(other.getCountry())
                    .locality(other.getLocality())
                    .poBox(other.getPoBox())
                    .postalCode(other.getPostalCode())
                    .premises(other.getPremises())
                    .region(other.getRegion());

        }

        public Builder addressLine1(final String value) {

            buildSteps.add(data -> data.addressLine1 = value);
            return this;
        }

        public Builder addressLine2(final String value) {

            buildSteps.add(data -> data.addressLine2 = value);
            return this;
        }

        public Builder careOf(final String value) {

            buildSteps.add(data -> data.careOf = value);
            return this;
        }

        public Builder country(final String value) {

            buildSteps.add(data -> data.country = value);
            return this;
        }

        public Builder locality(final String value) {

            buildSteps.add(data -> data.locality = value);
            return this;
        }

        public Builder poBox(final String value) {

            buildSteps.add(data -> data.poBox = value);
            return this;
        }

        public Builder postalCode(final String value) {

            buildSteps.add(data -> data.postalCode = value);
            return this;
        }

        public Builder premises(final String value) {

            buildSteps.add(data -> data.premises = value);
            return this;
        }

        public Builder region(final String value) {

            buildSteps.add(data -> data.region = value);
            return this;
        }

        public Address build() {

            final var data = new Address();
            buildSteps.forEach(step -> step.accept(data));
            return data;
        }
    }
}
