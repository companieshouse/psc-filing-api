package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

/**
 * Store/retrieve PSC Filing entities using the persistence layer.
 */
public interface PscFilingService {
    PscIndividualFiling save(PscIndividualFiling filing, String transactionId);

    PscWithIdentificationFiling save(PscWithIdentificationFiling filing, String transactionId);

    Optional<PscCommunal> get(String pscFilingId, String transactionId);
}
