package uk.gov.companieshouse.pscfiling.api.service;

import uk.gov.companieshouse.patch.service.PatchValidator;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

/**
 * Validator to ensure the Psc with identification patch contains valid data.
 */
public interface PscWithIdentificationPatchValidator extends PatchValidator<PscWithIdentificationFiling> {
}
