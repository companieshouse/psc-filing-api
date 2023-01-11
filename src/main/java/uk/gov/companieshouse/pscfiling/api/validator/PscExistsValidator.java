package uk.gov.companieshouse.pscfiling.api.validator;

import java.text.MessageFormat;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.error.ApiError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.error.ApiErrors;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscExistsValidator extends FilingValidatorImpl{

    private PscDetailsService pscDetailsService;

    public PscExistsValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    public ApiErrors validate(PscIndividualDto dto, ApiErrors errors, Transaction transaction,
                              PscTypeConstants pscType, String passthroughHeader) {

        var apiErrors = Optional.ofNullable(errors).orElseGet(ApiErrors::new);

        try {
                pscDetailsService.getPscDetails(transaction, dto.getReferencePscId(), pscType,
                    passthroughHeader);
        }
        catch (FilingResourceNotFoundException e) {

            apiErrors.add(new ApiError(
                MessageFormat.format("PSC Details not found for {0}: {1} {2}", dto.getReferencePscId(),
                    HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()), null, LocationType.RESOURCE.getValue(), ErrorType.SERVICE.getType()));

        }

        return super.validate(dto, apiErrors, transaction, pscType, passthroughHeader);
    }
}
