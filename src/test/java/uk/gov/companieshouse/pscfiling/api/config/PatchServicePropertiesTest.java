package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PatchServicePropertiesTest {

    @Test
    void setAndGetMaxRetries() {
        final PatchServiceProperties testProperties = new PatchServiceProperties();
        testProperties.setMaxRetries(5);

        assertThat(testProperties.getMaxRetries(), is(5));
    }

}