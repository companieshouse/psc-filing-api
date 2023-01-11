package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ApiErrors;
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
    private ApiErrors errors;
    private String passthroughHeader;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";

    @BeforeEach
    void setUp() {

        errors = new ApiErrors();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";

        testValidator = new PscExistsValidator(pscDetailsService);
    }

    @ParameterizedTest()
    @ValueSource(booleans = {true, false})
    void validateWhenPscExists(boolean nullInitialErrors) {

        when(dto.getReferencePscId()).thenReturn(PSC_ID);

        ApiErrors validateErrors =
            testValidator.validate(dto, nullInitialErrors ? null : errors, transaction, pscType, passthroughHeader);

        assertThat(validateErrors.getErrors(), is(empty()));
    }

    @ParameterizedTest()
    @ValueSource(booleans = {true, false})
    void validateWhenPscDoesNotExist(boolean nullInitialErrors) {

        var apiError = new ApiError("PSC Details not found for " + PSC_ID + ": 404 Not Found", null,
            LocationType.RESOURCE.getValue(), ErrorType.SERVICE.getType());
        when(dto.getReferencePscId()).thenReturn(PSC_ID);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType,
            passthroughHeader)).thenThrow(
            new FilingResourceNotFoundException("PSC Details not found for " + PSC_ID + ": 404 Not Found",
                errorResponseException));

        ApiErrors validateErrors =
            testValidator.validate(dto, nullInitialErrors ? null : errors, transaction, pscType, passthroughHeader);

        assertThat(validateErrors.getErrors().stream().findFirst().orElseThrow(), equalTo(apiError));
        assertThat(validateErrors.getErrors(), contains(apiError));
    }
}