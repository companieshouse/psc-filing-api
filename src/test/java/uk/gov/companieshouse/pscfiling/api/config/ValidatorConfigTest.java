package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.validator.CeasedOnDateValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscEtagValidator;
import uk.gov.companieshouse.pscfiling.api.validator.PscExistsValidator;

@ExtendWith(MockitoExtension.class)
class ValidatorConfigTest {
    private ValidatorConfig testConfig;
    @Mock
    private PscExistsValidator pscExistsValidator;
    @Mock
    private PscEtagValidator pscEtagValidator;
    @Mock
    private CeasedOnDateValidator ceasedOnDateValidator;

    @BeforeEach
    void setUp() {
        testConfig = new ValidatorConfig();
    }

    @Test
    void filingForIndividualValid() {
        final var valid = testConfig.filingForIndividualValid(pscExistsValidator, pscEtagValidator, ceasedOnDateValidator);

        assertThat(valid.getPscType(), is(PscTypeConstants.INDIVIDUAL));
        assertThat(valid.getFirst(), is(pscExistsValidator));
    }
}