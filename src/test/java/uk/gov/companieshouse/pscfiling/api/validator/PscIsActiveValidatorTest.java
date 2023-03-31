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
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@SpringBootTest()
class PscIsActiveValidatorTest {

    @MockBean
    private PscDetailsService pscDetailsService;
    @Mock
    private PscApi pscApi;
    @Mock
    private Transaction transaction;
    @Mock
    private PscIndividualDto dto;
    PscIsActiveValidator testValidator;
    private PscTypeConstants pscType;
    private List<FieldError> errors;
    private String passthroughHeader;
    @Autowired
    @Qualifier(value = "validation")
    private Map<String, String> validation;

    private static final String PSC_ID = "1kdaTltWeaP1EB70SSD9SLmiK5Y";
    private static final LocalDate CEASED_ON = LocalDate.of(2023,1,21);

    @BeforeEach
    void setUp() {

        errors = new ArrayList<>();
        pscType = PscTypeConstants.INDIVIDUAL;
        passthroughHeader = "passthroughHeader";

        testValidator = new PscIsActiveValidator(pscDetailsService, validation);
        when(dto.getReferencePscId()).thenReturn(PSC_ID);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, pscType, passthroughHeader )).thenReturn(pscApi);
    }

    @Test
    void validateWhenPSCisActive() {

        when(pscApi.getCeasedOn()).thenReturn(null);
        testValidator.validate(
            new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors, is(empty()));
    }

    @Test
    void validateWhenPSCisNotActive() {

        var fieldError = new FieldError("object", "ceased_on", CEASED_ON, false,
            new String[]{null, "date.ceased_on"}, null,
            "PSC is already ceased");

        when(dto.getCeasedOn()).thenReturn(CEASED_ON);
        when(pscApi.getCeasedOn()).thenReturn(CEASED_ON);

        testValidator.validate(
            new FilingValidationContext<>(dto, errors, transaction, pscType, passthroughHeader));

        assertThat(errors.stream().findFirst().orElseThrow(), equalTo(fieldError));
        assertThat(errors, contains(fieldError));
    }
}