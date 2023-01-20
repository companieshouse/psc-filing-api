package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;

public interface FilingValidationService {

    /**
     * Apply the chain of validation steps appropriate for the given PSC type.
     *
     * @param context the filing data to be validated, with supporting context
     */
    void validate(FilingValidationContext context);
}
