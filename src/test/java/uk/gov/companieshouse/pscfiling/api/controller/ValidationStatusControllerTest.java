package uk.gov.companieshouse.pscfiling.api.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import javax.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;



class ValidationStatusControllerTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void validate() {
        var testController = new ValidationStatusController() {};

        assertThrows(NotImplementedException.class, () -> testController.validate("trans-id", "filing-id", request));
    }
}