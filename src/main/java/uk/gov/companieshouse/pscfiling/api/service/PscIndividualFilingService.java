package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Map;
import java.util.Optional;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.patch.service.PatchService;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

public interface PscIndividualFilingService extends PatchService<PscIndividualFiling> {
    PscIndividualFiling createFiling(final PscIndividualFiling filing);

    Optional<PscIndividualFiling> getFiling(final String filingId);

    PatchResult updateFiling(final String filingId, final Map<String, Object> patchMap);

}
