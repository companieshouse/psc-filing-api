package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValid;
import uk.gov.companieshouse.pscfiling.api.validator.FilingForPscTypeValidChain;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValid;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;

@ExtendWith(MockitoExtension.class)
class FilingValidationServiceImplTest {
    private FilingValidationService testService;
    private List<? extends FilingForPscTypeValid> forPscTypeValids;
    @Mock
    private FilingValid firstFilingValid;
    @Mock
    private FilingValidationContext context;

    @BeforeEach
    void setUp() {
        forPscTypeValids = List.of(new FilingForPscTypeValidChain(PscTypeConstants.INDIVIDUAL,
                firstFilingValid));
        testService = new FilingValidationServiceImpl(forPscTypeValids);
    }

    @Test
    void validate() {
        when(context.getPscType()).thenReturn(PscTypeConstants.INDIVIDUAL);

        testService.validate(context);

        verify(firstFilingValid).validate(context);

    }

    @Test
    void validateWhenPscTypeNotSupported() {
        when(context.getPscType()).thenReturn(PscTypeConstants.CORPORATE_ENTITY);

        final var exception = assertThrows(UnsupportedOperationException.class,
                () -> testService.validate(context));

        assertThat(exception.getMessage(), is("Validation not defined for PSC type 'CORPORATE_ENTITY'"));

    }
}