package uk.gov.companieshouse.pscfiling.api.validator;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;

@Component
public class PscExistsValidator extends BaseFilingValidator implements FilingPermissible {

    private PscDetailsService pscDetailsService;

    public PscExistsValidator(PscDetailsService pscDetailsService) {
        this.pscDetailsService = pscDetailsService;
    }

    @Override
    public void validate(final PscIndividualDto dto, final List<FieldError> errors,
            final Transaction transaction, final PscTypeConstants pscType,
            final String passthroughHeader) {

        try {
                pscDetailsService.getPscDetails(transaction, dto.getReferencePscId(), pscType,
                    passthroughHeader);
        }
        catch (FilingResourceNotFoundException e) {
            errors.add(
                    new FieldError("object", "reference_psc_id", dto.getReferencePscId(), false,
                            new String[]{null, "notFound.reference_psc_id"}, null, e.getMessage()));
        }

        super.validate(dto, errors, transaction, pscType, passthroughHeader);
    }

}
