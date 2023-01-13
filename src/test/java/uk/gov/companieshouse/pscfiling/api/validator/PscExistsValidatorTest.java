package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@ExtendWith(MockitoExtension.class)
class PscExistsValidatorTest {

    @Mock
    private PscDetailsService pscDetailsService;
    @Mock
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;
    @Mock
    private ApiErrorResponseException errorResponseException;

    PscExistsValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passthroughHeader;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";

    @BeforeEach
    void setUp() {

        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";

        testValidator = new PscExistsValidator(pscDetailsService);
    }

    @Test
    void validateWhenPscExists() {

        when(dto.getReferencePscId()).thenReturn(PSC_ID);

        testValidator.validate(dto, errors, transaction, pscType, passthroughHeader);

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenPscDoesNotExist() {

        var apiError = new ApiError("PSC Details not found for " + PSC_ID + ": 404 Not Found", null,
                LocationType.RESOURCE.getValue(), ErrorType.SERVICE.getType());
        when(dto.getReferencePscId()).thenReturn(PSC_ID);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType,
                passthroughHeader)).thenThrow(new FilingResourceNotFoundException(
                "PSC Details not found for " + PSC_ID + ": 404 Not Found", errorResponseException));

        testValidator.validate(dto, errors, transaction, pscType, passthroughHeader);

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(apiError));
        assertThat(errors, contains(apiError));
    }
}