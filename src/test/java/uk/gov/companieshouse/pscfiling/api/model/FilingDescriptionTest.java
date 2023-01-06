package uk.gov.companieshouse.pscfiling.api.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilingDescriptionTest {

    private FilingDescription filingDescription;

    @BeforeEach
    void setup() {
        filingDescription = new FilingDescription();
    }

    @Test
    void getPsc07() {
        filingDescription.setPsc07("Notice of ceasing to be a Person of Significant Control");

        assertThat(filingDescription.getPsc07(), is("Notice of ceasing to be a Person of Significant Control"));
    }

}

