package uk.gov.companieshouse.pscfiling.api.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilingKindTest {

    @Test
    void getValue() {
        assertThat(FilingKind.PSC_CESSATION.getValue(), is("psc-filing#cessation"));
    }

    @Test
    void nameOfWhenFound() {
        assertThat(FilingKind.nameOf("psc-filing#cessation"), is(Optional.of(FilingKind.PSC_CESSATION)));
    }

    @Test
    void nameOfWhenNotFound() {
        assertThat(FilingKind.nameOf("test"), is(Optional.empty()));
    }

}