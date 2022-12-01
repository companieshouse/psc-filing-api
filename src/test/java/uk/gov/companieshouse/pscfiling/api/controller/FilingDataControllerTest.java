package uk.gov.companieshouse.pscfiling.api.controller;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilingDataControllerTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void getFilingsData() {

        var testController = new FilingDataController(){};

        assertThrows(NotImplementedException.class,
            () -> testController.getFilingsData("trans-id", "filing-id", request));
    }
}