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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
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
    @Mock
    private ApiErrorResponseException errorResponseException;

    CeasedOnDateValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passthroughHeader;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private final LocalDate date = LocalDate.of(2020, 5, 10);
    private final LocalDate dayAfterDate = LocalDate.of(2020, 5, 11);

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType, passthroughHeader)).thenReturn(pscApi);
        when(dto.getReferencePscId()).thenReturn(PSC_ID);

        testValidator = new CeasedOnDateValidator(pscDetailsService);
    }

    @Test
    void validateWhenCeasedOnAfterNotifiedOn() {
        when(dto.getCeasedOn()).thenReturn(dayAfterDate);
        when(pscApi.getNotifiedOn()).thenReturn(date);

        testValidator.validate(new FilingValidationContext(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenCeasedOnSameAsNotifiedOn() {
        when(dto.getCeasedOn()).thenReturn(date);
        when(pscApi.getNotifiedOn()).thenReturn(date);

        testValidator.validate(new FilingValidationContext(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenCeasedOnBeforeNotifiedOn() {
        var fieldError = new FieldError("object", "ceased_on", date, false, new String[]{null, "date.ceased_on"},
                null,"Ceased on date is before the date the PSC was notified on");
        when(dto.getCeasedOn()).thenReturn(date);
        when(pscApi.getNotifiedOn()).thenReturn(dayAfterDate);

        testValidator.validate(new FilingValidationContext(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }

}

