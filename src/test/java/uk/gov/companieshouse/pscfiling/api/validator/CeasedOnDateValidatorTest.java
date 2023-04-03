package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@ExtendWith(MockitoExtension.class)
class CeasedOnDateValidatorTest {
    @Mock
    private PscDetailsService pscDetailsService;
    @Mock
    private PscApi pscApi;
    @Mock
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;

    private CeasedOnDateValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passthroughHeader;
    @Mock
    private Map<String, String> validation;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private static final LocalDate DATE = LocalDate.of(2020, 5, 10);
    private static final LocalDate DAY_AFTER_DATE = LocalDate.of(2020, 5, 11);

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType, passthroughHeader)).thenReturn(pscApi);
        when(dto.getReferencePscId()).thenReturn(PSC_ID);

        testValidator = new CeasedOnDateValidator(pscDetailsService, validation);
    }

    public static Stream<Arguments> provideDates() {
        return Stream.of(
                Arguments.of(DAY_AFTER_DATE, DATE),
                Arguments.of(DATE, DATE),
                Arguments.of(null, DATE),
                Arguments.of(DATE, null),
                Arguments.of(null, null))
                ;
    }

    @ParameterizedTest(name = ARGUMENTS_WITH_NAMES_PLACEHOLDER)
    @MethodSource({"provideDates"})
    void validateCeasedOnAndNotifiedOnDates(final LocalDate ceasedOn, final LocalDate notifiedOn) {
        when(dto.getCeasedOn()).thenReturn(ceasedOn);
        if (ceasedOn != null) {
            when(pscApi.getNotifiedOn()).thenReturn(notifiedOn);
            if (notifiedOn != null && ceasedOn.isBefore(notifiedOn)) {
                when(validation.get("ceased-date-before-notified-date")).thenReturn(
                        "date-before default message");
            }
        }

        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenCeasedOnBeforeNotifiedOn() {
        final var fieldError = new FieldError("object", "ceased_on", DATE, false, new String[]{null, "date.ceased_on"},
                null, "date-before default message");
        when(dto.getCeasedOn()).thenReturn(DATE);
        when(pscApi.getNotifiedOn()).thenReturn(DAY_AFTER_DATE);
        when(validation.get("ceased-date-before-notified-date")).thenReturn("date-before default message");

        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, contains(fieldError));
    }

}

