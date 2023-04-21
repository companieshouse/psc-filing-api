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
    PscIndividualFiling save(PscIndividualFiling filing);

    PscWithIdentificationFiling save(PscWithIdentificationFiling filing);

    Optional<PscCommunal> get(String pscFilingId);

    boolean requestMatchesResourceSelf(HttpServletRequest request, PscCommunal pscFiling);
}
