package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

@ExtendWith(MockitoExtension.class)
class FilingValidationContextTest {
    private static final String PASSTHROUGH_HEADER = "passthrough";
    private FilingValidationContext testContext;
    @Mock
    private PscIndividualDto dto;
    @Mock
    private Transaction transaction;
    private List<FieldError> errors;

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        testContext =
                new FilingValidationContext(dto, errors, transaction, PscTypeConstants.INDIVIDUAL,
                        PASSTHROUGH_HEADER);
    }

    @Test
    void constructorWhenDtoNull() {
        assertThrows(NullPointerException.class,
                () -> new FilingValidationContext(null, errors, transaction,
                        PscTypeConstants.INDIVIDUAL, PASSTHROUGH_HEADER));
    }

    @Test
    void constructorWhenErrorsNull() {
        assertThrows(NullPointerException.class,
                () -> new FilingValidationContext(dto, null, transaction,
                        PscTypeConstants.INDIVIDUAL, PASSTHROUGH_HEADER));
    }

    void constructorWhenTransactionNull() {
        assertThrows(NullPointerException.class,
                () -> new FilingValidationContext(dto, errors, null,
                        PscTypeConstants.INDIVIDUAL, PASSTHROUGH_HEADER));
    }

    void constructorWhenPscTypeNull() {
        assertThrows(NullPointerException.class,
                () -> new FilingValidationContext(dto, errors, transaction,
                        null, PASSTHROUGH_HEADER));
    }

    @Test
    void getDto() {
        assertThat(testContext.getDto(), is(sameInstance(dto)));
    }

    @Test
    void getErrors() {
        assertThat(testContext.getErrors(), is(errors));
    }

    @Test
    void getTransaction() {
        assertThat(testContext.getTransaction(), is(sameInstance(transaction)));
    }

    @Test
    void getPscType() {
        assertThat(testContext.getPscType(), is(PscTypeConstants.INDIVIDUAL));
    }

    @Test
    void getPassthroughHeader() {
        assertThat(testContext.getPassthroughHeader(), is(PASSTHROUGH_HEADER));
    }

    @Test
    void testEquals() {
        EqualsVerifier.forClass(FilingValidationContext.class).usingGetClass().verify();
    }

    @Test
    void testToString() {
        assertThat(testContext.toString(),
                is("FilingValidationContext[dto=dto, errors=[], transaction=transaction, "
                        + "pscType=INDIVIDUAL, passthroughHeader='passthrough']"));
    }
}