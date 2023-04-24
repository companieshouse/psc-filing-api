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
    @Deprecated
    PscIndividualFiling save(PscIndividualFiling filing, String transactionId);

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
     * @param transactionId the associated Transaction ID
     * @return the stored entity
     */
    PscWithIdentificationFiling save(PscWithIdentificationFiling filing, String transactionId);

    @Deprecated
    Optional<PscCommunal> get(String pscFilingId, String transactionId);

    /**
     * Retrieve a stored PSCFiling entity by Filing ID.
     *
     * @param pscFilingId   the Filing ID
     * @return the stored entity if found
     */
    Optional<PscCommunal> get(String pscFilingId);

    //TODO - review if this method is needed now we have provider classes - for PATCH
    boolean requestMatchesResource(HttpServletRequest request, PscCommunal pscFiling);
}
