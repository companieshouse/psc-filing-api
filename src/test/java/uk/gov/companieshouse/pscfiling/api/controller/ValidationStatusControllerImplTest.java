package uk.gov.companieshouse.pscfiling.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.pscfiling.api.controller.ValidationStatusControllerImpl.TRANSACTION_NOT_SUPPORTED_ERROR;

@ExtendWith(MockitoExtension.class)
class ValidationStatusControllerImplTest {

    public static final String TRANS_ID = "117524-754816-491724";
    public static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    @Mock
    private PscFilingService pscFilingService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Logger logger;

    private ValidationStatusControllerImpl testController;

    @BeforeEach
    void setUp() {

    }

    @ParameterizedTest(name = "Run {index}: PSC Individual Found isClosable {0}")
    @ValueSource(booleans = {true, false})
    void validateWhenPscIndividualFoundAndFlagTrue(boolean isClosable) {
        testController = new ValidationStatusControllerImpl(pscFilingService, isClosable, logger );
        var filing = PscIndividualFiling.builder().build();
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        final var response= testController.validate(TRANS_ID, FILING_ID, request);

        assertThat(response.isValid(), is(isClosable));

        if(isClosable) {
            assertThat(response.getValidationStatusError(), is(nullValue()));

        } else{
            assertThat(response.getValidationStatusError(), is(arrayWithSize(1)));
            assertThat(response.getValidationStatusError()[0].getError(), is(TRANSACTION_NOT_SUPPORTED_ERROR));
        }
    }

    @Test
    void validateWhenNotFound() {
        testController = new ValidationStatusControllerImpl(pscFilingService, true, logger );
        when(pscFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.empty());

        final var filingResourceNotFoundException =
                assertThrows(FilingResourceNotFoundException.class,
                        () -> testController.validate(TRANS_ID, FILING_ID, request));

        assertThat(filingResourceNotFoundException.getMessage(), containsString(FILING_ID));
    }

}
