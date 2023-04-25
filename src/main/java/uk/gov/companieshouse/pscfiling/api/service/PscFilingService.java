package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

/**
 * Store/retrieve PSC Filing entities using the persistence layer.
 */
public interface PscFilingService {

    /**
     * Store a PSCIndividualFiling entity in persistence layer.
     *
     * @param filing        the PSCIndividualFiling entity to store
     * @return the stored entity
     */
    PscIndividualFiling save(PscIndividualFiling filing);

    /**
     * Store a PSCWithIdentificationFiling entity in persistence layer.
     *
     * @param filing        the PSCWithIdentificationFiling entity to store
     * @return the stored entity
     */
    PscWithIdentificationFiling save(PscWithIdentificationFiling filing);

    @Deprecated
    Optional<PscCommunal> get(String pscFilingId, String transactionId);

    /**
     * Retrieve a stored PSCFiling entity by Filing ID.
     *
     * @param pscFilingId   the Filing ID
     * @return the stored entity if found
     */
    Optional<PscCommunal> get(String pscFilingId);

    boolean requestMatchesResourceSelf(HttpServletRequest request, PscCommunal pscFiling);
}
