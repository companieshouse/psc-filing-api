package uk.gov.companieshouse.pscfiling.api.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.config.FilingDataConfig;

@ExtendWith(MockitoExtension.class)
class FilingDataConfigTest {

    private FilingDataConfig filingDataConfig;

    @BeforeEach
    void setUp() {
        filingDataConfig = new FilingDataConfig();
    }

    @Test
    void getPsc07() {
        filingDataConfig.setPsc07Description("Notice of ceasing to be a Person of Significant Control");

        assertThat(filingDataConfig.getPsc07Description(), is("Notice of ceasing to be a Person of Significant Control"));
    }

}

