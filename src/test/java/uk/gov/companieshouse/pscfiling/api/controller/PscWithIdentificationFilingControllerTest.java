package uk.gov.companieshouse.pscfiling.api.controller;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PscWithIdentificationFilingControllerTest {

    private final PscWithIdentificationFilingController testController =
            new PscWithIdentificationFilingController() { };
    @Mock
    private PscWithIdentificationDto dto;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletRequest request;

    @Test
    void createFiling() {
        assertThrows(NotImplementedException.class,
                () -> testController.createFiling("trans-id", PscTypeConstants.CORPORATE_ENTITY, dto,
                        bindingResult, request));
    }

    @Test
    void getFilingForReview() {
        assertThrows(NotImplementedException.class,
                () -> testController.getFilingForReview("trans-id", PscTypeConstants.CORPORATE_ENTITY,
                        "filing-resource-id"));
    }
}