package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.patch.service.PatchValidator;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

/**
 * Validator to ensure the Psc individual patch contains valid data.
 */
public interface PscIndividualPatchValidator  extends PatchValidator<PscIndividualFiling> {
}
