package uk.gov.companieshouse.pscfiling.api.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PscTypeConstantsEnumConverterTest {
    private PscTypeConstantsEnumConverter testConverter;

    @BeforeEach
    void setUp() {
        testConverter = new PscTypeConstantsEnumConverter();
    }

    @ParameterizedTest(name = "[{index}] convert '{0}'")
    @EnumSource(PscTypeConstants.class)
    void convertValidValue(final PscTypeConstants value) {
        assertThat(testConverter.convert(value.getValue()), is(value));
    }

    @Test
    @DisplayName("convert 'banana'")
    void convertInvalidValue()
    {
        final var exception =
                assertThrows(IllegalArgumentException.class, () -> testConverter.convert("banana"));

        assertThat(exception.getMessage(), is("No such PSC Type: banana"));

    }

}