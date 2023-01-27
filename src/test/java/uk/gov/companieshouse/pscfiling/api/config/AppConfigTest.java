package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {
    private AppConfig testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new AppConfig();
    }

    @Test
    void clock() {
        assertThat(testConfig.clock().getZone(), is(ZoneOffset.UTC));
    }
}