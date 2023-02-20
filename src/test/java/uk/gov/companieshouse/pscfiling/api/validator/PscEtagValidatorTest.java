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
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@ExtendWith(MockitoExtension.class)
class PscEtagValidatorTest {

    @Mock
    private PscDetailsService pscDetailsService;
    @Mock
    private PscApi pscApi;
    @Mock
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;

    PscEtagValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passthroughHeader;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private static final String ETAG = "1234567";

    @BeforeEach
    void setUp() {
        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";

        when(dto.getReferencePscId()).thenReturn(PSC_ID);
        when(dto.getReferenceEtag()).thenReturn(ETAG);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType, passthroughHeader )).thenReturn(pscApi);

        testValidator = new PscEtagValidator(pscDetailsService);
    }

    @Test
    void validateWhenEtagMatches() {
        when(pscApi.getEtag()).thenReturn(ETAG);

        testValidator.validate(
                new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenEtagDoesNotMatch() {
        var fieldError = new FieldError("object", "reference_etag", ETAG, false,
                new String[]{null, "notMatch.reference_etag"}, null,
                "Etag for PSC does not match latest value");
        when(pscApi.getEtag()).thenReturn("some other etag value");

        testValidator.validate(
                new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }
}

