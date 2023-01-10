package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ApiErrors;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.InvalidFilingException;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscExistsValidator {

    private PscDetailsService pscDetailsService;

    public PscExistsValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    public ApiErrors validate(PscIndividualDto dto, ApiErrors errors, Transaction transaction,
                              PscTypeConstants pscType, String passthroughHeader) {

        var apiErrors = Optional.ofNullable(errors).orElseGet(ApiErrors::new);


        final PscApi pscDetails;
        try {
            pscDetails =
                pscDetailsService.getPscDetails(transaction, dto.getReferencePscId(), pscType,
                    passthroughHeader);
        }
        catch (FilingResourceNotFoundException e) {

            apiErrors.add(new ApiError("Psc not found for this company.", null, LocationType.RESOURCE.getValue(), ErrorType.SERVICE.getType()));

        }

        return apiErrors;
    }
}
