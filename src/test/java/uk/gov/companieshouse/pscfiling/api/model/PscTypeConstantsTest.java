package uk.gov.companieshouse.pscfiling.api.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PscTypeConstantsTest {

    @Test
    void getValue() {
        assertThat(PscTypeConstants.INDIVIDUAL.getValue(), is("individual"));
    }

    @Test
    void nameOfWhenFound() {
        assertThat(PscTypeConstants.nameOf("individual"), is(Optional.of(PscTypeConstants.INDIVIDUAL)));
    }

    @Test
    void nameOfWhenNotFound() {
        assertThat(PscTypeConstants.nameOf("banana"), is(Optional.empty()));
    }
}