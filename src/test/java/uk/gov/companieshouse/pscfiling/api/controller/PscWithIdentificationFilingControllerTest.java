package uk.gov.companieshouse.pscfiling.api.controller;

import java.util.Collections;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.NotImplementedException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PscWithIdentificationFilingControllerTest {

    private final PscWithIdentificationFilingController testController =
            new PscWithIdentificationFilingController() { };
    @Mock
    private PscWithIdentificationDto dto;
    @Mock
    private Transaction transaction;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpServletRequest request;

    @Test
    void createFiling() {
        assertThrows(NotImplementedException.class,
                () -> testController.createFiling("trans-id", PscTypeConstants.CORPORATE_ENTITY, transaction,
                        dto, bindingResult, request));
    }

    @Test
    void getFilingForReview() {
        assertThrows(NotImplementedException.class,
                () -> testController.getFilingForReview("trans-id", PscTypeConstants.CORPORATE_ENTITY,
                        "filing-resource-id", request));
    }

    @Test
    void updateFiling() {
        Map<String,Object> map = Collections.emptyMap();

        assertThrows(NotImplementedException.class,
                () -> testController.updateFiling("trans-id", PscTypeConstants.CORPORATE_ENTITY, "filingResourceId",
                        map, request));
    }

}