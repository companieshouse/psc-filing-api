package uk.gov.companieshouse.pscfiling.api.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@SpringBootTest
class PscExistsValidatorTest {

    @MockBean
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
    @Autowired
    @Qualifier(value = "validation")
    private Map<String, String> validation;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";

    @BeforeEach
    void setUp() {

        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";

        testValidator = new PscExistsValidator(pscDetailsService, validation);
    }

    @Test
    void validateWhenPscExists() {

        when(dto.getReferencePscId()).thenReturn(PSC_ID);

        testValidator.validate(
                new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenPscDoesNotExist() {

        var fieldError = new FieldError("object", "reference_psc_id", PSC_ID, false,
                new String[]{null, "notFound.reference_psc_id"}, null,
                "Reference ID for PSC not found");
        when(dto.getReferencePscId()).thenReturn(PSC_ID);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType,
                passthroughHeader)).thenThrow(new FilingResourceNotFoundException(
                "PSC Details not found for " + PSC_ID + ": 404 Not Found", errorResponseException));

        testValidator.validate(
                new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }
}