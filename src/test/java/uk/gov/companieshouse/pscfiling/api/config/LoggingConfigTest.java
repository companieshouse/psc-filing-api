package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class LoggingConfigTest {
    private LoggingConfig testConfig;

    @BeforeEach
    void setUp() {
        testConfig = new LoggingConfig();
    }

    @Test
    void logger() {
        assertThat(testConfig.logger(), isA(Logger.class));
    }
}