package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Map;
import java.util.Optional;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.patch.service.PatchService;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

/**
 * Psc with identification filing service layer.
 */
public interface PscWithIdentificationFilingService extends PatchService<PscWithIdentificationFiling> {

    /**
     * Store a PSCWithIdentificationFiling entity in persistence layer.
     *
     * @param filing the PSCWithIdentificationFiling entity to store
     * @return the stored entity
     */
    PscWithIdentificationFiling save(final PscWithIdentificationFiling filing);

    /**
     * Retrieve a stored PSCWithIdentificationFiling entity by Filing ID.
     *
     * @param filingId   the Filing ID
     * @return the stored entity if found
     */
    Optional<PscWithIdentificationFiling> get(final String filingId);

    /**
     * Update a PSCFiling entity by Filing ID.
     *
     * @param filingId   the Filing ID
     * @param patchMap   a list of parameters to include in the patch
     * @return the patch result
     */
    PatchResult patch(final String filingId, final Map<String, Object> patchMap);

}
