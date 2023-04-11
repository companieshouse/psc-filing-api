package uk.gov.companieshouse.pscfiling.api.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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

    @Test
    void objectMapperBuilder() {
        assertThat(testConfig.objectMapperBuilder(), isA(Jackson2ObjectMapperBuilder.class));
    }

    @Test
    void objectMapper() {
        assertThat(testConfig.objectMapper(), isA(ObjectMapper.class));
    }

    @Test
    void patchObjectMapper() {
        assertThat(testConfig.patchObjectMapper(), isA(ObjectMapper.class));
    }
}