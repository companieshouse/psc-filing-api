package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
class PscRegisterEntryDateValidatorTest {

    @Mock
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;

    PscRegisterEntryDateValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passThroughHeader;
    private static final LocalDate DATE = LocalDate.of(2023,1,21);
    private static final LocalDate BEFORE = LocalDate.of(2023,1,20);
    private static final LocalDate AFTER_DATE = LocalDate.of(2023,1,22);

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passThroughHeader = "passThroughHeader";
        testValidator = new PscRegisterEntryDateValidator();

        when(dto.getCeasedOn()).thenReturn(DATE);
    }

    @Test
    void validateWhenPscRegisterEntryDateAfterCessationDate() {
        when(dto.getRegisterEntryDate()).thenReturn(AFTER_DATE);

        testValidator.validate(
                new FilingValidationContext(dto, errors, transaction, pscType, passThroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenPscRegisterEntryDateEqualsCessationDate() {
        when(dto.getRegisterEntryDate()).thenReturn(DATE);

        testValidator.validate(
                new FilingValidationContext(dto, errors, transaction, pscType, passThroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenPscRegisterEntryDateBeforeCessationDate() {
        var fieldError = new FieldError("object", "register_entry_date", BEFORE, false,
                new String[]{null, "date.register_entry_date"}, null,
                "PSC register entry date cannot be before the cessation date");

        when(dto.getRegisterEntryDate()).thenReturn(BEFORE);

        testValidator.validate(
                new FilingValidationContext(dto, errors, transaction, pscType, passThroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }

}