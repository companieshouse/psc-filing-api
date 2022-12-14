package uk.gov.companieshouse.pscfiling.api.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;

@ExtendWith(MockitoExtension.class)
class PscFilingControllerTest {

    private final PscFilingController testController = new PscFilingController() {
    };
    @Mock
    private PscIndividualDto dto;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletRequest request;

    @Test
    void createFiling() {
        assertThrows(NotImplementedException.class,
                () -> testController.createFiling("trans-id", PscTypeConstants.INDIVIDUAL, dto,
                        bindingResult, request));
    }

    @Test
    void getFilingForReview() {
        assertThrows(NotImplementedException.class,
                () -> testController.getFilingForReview("trans-id", PscTypeConstants.INDIVIDUAL,
                        "filing-resource-id"));
    }
}