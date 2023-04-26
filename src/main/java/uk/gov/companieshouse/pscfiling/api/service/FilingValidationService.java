package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.validator.FilingValidationContext;

/**
 * The filing validation service layer that passes to the chain of validators
 * as defined in ValidatorConfig.
 */
public interface FilingValidationService {

    /**
     * Apply the chain of validation steps appropriate for the given PSC type.
     *
     * @param context the filing data to be validated, with supporting context
     */
    <T extends PscDtoCommunal> void validate(FilingValidationContext<T> context);
}