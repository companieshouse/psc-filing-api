package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Map;
import java.util.Optional;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.patch.service.PatchService;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

/**
 * Psc individual filing service layer.
 */
public interface PscIndividualFilingService extends PatchService<PscIndividualFiling> {

    /**
     * Store a PSCIndividualFiling entity in persistence layer.
     *
     * @param filing the PSCIndividualFiling entity to store
     * @return the stored entity
     */
    PscIndividualFiling createFiling(final PscIndividualFiling filing);

    /**
     * Retrieve a stored PSCIndividualFiling entity by Filing ID.
     *
     * @param filingId   the Filing ID
     * @return the stored entity if found
     */
    Optional<PscIndividualFiling> getFiling(final String filingId);

    /**
     * Update a PSCFiling entity by Filing ID.
     *
     * @param filingId   the Filing ID
     * @param patchMap   a list of parameters to include in the patch
     * @return the patch result
     */
    PatchResult updateFiling(final String filingId, final Map<String, Object> patchMap);

}
