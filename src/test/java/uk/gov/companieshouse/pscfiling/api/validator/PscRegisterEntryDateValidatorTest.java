package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

@SpringBootTest
class PscRegisterEntryDateValidatorTest {

    @MockBean
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;

    PscRegisterEntryDateValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passThroughHeader;
    private static final LocalDate DATE = LocalDate.of(2023, 1, 21);
    private static final LocalDate BEFORE_DATE = LocalDate.of(2023, 1, 20);
    private static final LocalDate AFTER_DATE = LocalDate.of(2023, 1, 22);
    @Autowired
    @Qualifier(value = "validation")
    private Map<String, String> validation;

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;

        passThroughHeader = "passThroughHeader";
        testValidator = new PscRegisterEntryDateValidator(validation);

        when(dto.getCeasedOn()).thenReturn(DATE);
    }

    @Test
    void validateWhenPscRegisterEntryDateBeforeCessationDate() {
        var fieldError = new FieldError("object", "register_entry_date", BEFORE_DATE, false,
                new String[]{null, "date.register_entry_date"}, null,
                "PSC register entry date must be on or after the date the PSC was ceased");

        when(dto.getRegisterEntryDate()).thenReturn(BEFORE_DATE);

        testValidator.validate(
                new FilingValidationContext<>(dto, errors, transaction, pscType, passThroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }


    @ParameterizedTest(name = ARGUMENTS_WITH_NAMES_PLACEHOLDER)
    @MethodSource("provideDates")
    void validateDates(final LocalDate ceasedOnDate,
            final LocalDate registerEntryDate) {

        when(dto.getCeasedOn()).thenReturn(ceasedOnDate);
        when(dto.getRegisterEntryDate()).thenReturn(registerEntryDate);

        testValidator.validate(
                new FilingValidationContext<>(dto, errors, transaction, pscType, passThroughHeader));

        assertThat(errors, is(empty()));
    }

    public static Stream<Arguments> provideDates() {
        return Stream.of(Arguments.of(DATE, DATE), Arguments.of(DATE, AFTER_DATE));
    }

}