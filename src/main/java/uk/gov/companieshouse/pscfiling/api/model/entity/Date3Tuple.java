package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Representation of Full and Partial Dates of Birth.
 *
 * <table>
 *     <tr>
 *         <td></td> <td>Day</td> <td>Month</td> <td>Year</td>
 *     </tr>
 *     <tr>
 *         <td>Full</td> <td>o</td> <td>o</td> <td>o</td>
 *     </tr>
 *     <tr>
 *         <td>Partial</td> <td>x</td> <td>o</td> <td>o</td>
 *     </tr>
 *  <p>o = required</p>
 *  <p>x = forbidden</p>
 * </table>
 */
public record Date3Tuple(int day, int month, int year) {
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Date3Tuple that = (Date3Tuple) o;
        return Objects.equals(day(), that.day()) && Objects.equals(month(),
                that.month()) && Objects.equals(year(), that.year());
    }

    @Override
    public int hashCode() {
        return Objects.hash(day(), month(), year());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Date3Tuple.class.getSimpleName() + "[", "]").add(
                "day=" + day).add("month=" + month).add("year=" + year).toString();
    }
}
