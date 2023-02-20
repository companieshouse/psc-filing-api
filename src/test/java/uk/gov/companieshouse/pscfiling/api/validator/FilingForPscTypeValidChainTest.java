package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

@ExtendWith(MockitoExtension.class)
class FilingForPscTypeValidChainTest {
    private FilingForPscTypeValidChain testChain;
    @Mock
    private IndividualFilingValid firstValid;

    @BeforeEach
    void setUp() {
        testChain = new FilingForPscTypeValidChain(PscTypeConstants.INDIVIDUAL, firstValid);
    }

    @Test
    void constructorWhenPscTypeNull() {
        assertThrows(NullPointerException.class,
                () -> new FilingForPscTypeValidChain(null, firstValid));
    }

    @Test
    void constructorWhenFirstNull() {
        assertThrows(NullPointerException.class,
                () -> new FilingForPscTypeValidChain(PscTypeConstants.INDIVIDUAL, null));
    }

    @Test
    void getPscType() {
        assertThat(testChain.getPscType(), is(PscTypeConstants.INDIVIDUAL));
    }

    @Test
    void getFirst() {
        assertThat(testChain.getFirst(), is(sameInstance(firstValid)));
    }

    @Test
    void testToString() {
        assertThat(testChain.toString(),
                is("FilingForPscTypeValidChain[pscType=INDIVIDUAL, first=firstValid]"));
    }
}