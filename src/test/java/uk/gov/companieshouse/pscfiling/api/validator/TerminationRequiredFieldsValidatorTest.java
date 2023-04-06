package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
class TerminationRequiredFieldsValidatorTest {

    @Mock
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;
    @Mock
    private Map<String, String> validation;

    TerminationRequiredFieldsValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passthroughHeader;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private static final String ETAG = "1234567";
    private static final LocalDate DATE = LocalDate.of(2020, 5, 10);

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";

        when(dto.getReferencePscId()).thenReturn(PSC_ID);
        when(dto.getCeasedOn()).thenReturn(DATE);
        when(dto.getRegisterEntryDate()).thenReturn(DATE);
        when(dto.getReferenceEtag()).thenReturn(ETAG);

        testValidator = new TerminationRequiredFieldsValidator(validation);
    }

    @Test
    void validateAllDataPresent() {
        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validatePscIdNotPresent() {
        var fieldError =
                new FieldError("object", "reference_psc_id", null, false, new String[]{null, "reference_psc_id"}, null,
                        "Reference PSC ID must be entered");
        when(dto.getReferencePscId()).thenReturn(null);
        when(validation.get("reference-psc-id-missing")).thenReturn(
                "Reference PSC ID must be entered");

        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }

    @Test
    void validateEtagNotPresent() {
        var fieldError =
                new FieldError("object", "reference_etag", null, false, new String[]{null, "reference_etag"}, null,
                        "Reference ETag must be entered");
        when(dto.getReferenceEtag()).thenReturn(null);
        when(validation.get("reference-etag-missing")).thenReturn(
                "Reference ETag must be entered");

        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }

    @Test
    void validateCeasedOnDateNotPresent() {
        var fieldError = new FieldError("object", "ceased_on", null, false, new String[]{null, "ceased_on"}, null,
                "Ceased date must be entered");
        when(dto.getCeasedOn()).thenReturn(null);
        when(validation.get("ceased-date-missing")).thenReturn(
                "Ceased date must be entered");

        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }

    @Test
    void validateRegisterEntryDateNotPresent() {
        var fieldError = new FieldError("object", "register_entry_date", null, false, new String[]{null, "register_entry_date"}, null,
                "Register entry date must be entered");
        when(dto.getRegisterEntryDate()).thenReturn(null);
        when(validation.get("register-date-missing")).thenReturn(
                "Register entry date must be entered");

        testValidator.validate(new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }
}

