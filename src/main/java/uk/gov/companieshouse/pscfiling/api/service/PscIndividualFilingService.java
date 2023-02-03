package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

/**
 * Store/retrieve PSC Filing entities using the persistence layer.
 */
public interface PscIndividualFilingService {
    PscIndividualFiling save(PscIndividualFiling filing, String transactionId);

    Optional<PscIndividualFiling> get(String pscFilingId, String transactionId);

}
