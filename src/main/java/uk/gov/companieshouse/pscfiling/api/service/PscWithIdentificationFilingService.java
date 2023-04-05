package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Map;
import java.util.Optional;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.patch.service.PatchService;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

public interface PscWithIdentificationFilingService extends PatchService<PscWithIdentificationFiling> {
    PscWithIdentificationFiling createFiling(final PscWithIdentificationFiling filing);

    Optional<PscWithIdentificationFiling> getFiling(final String filingId);

    PatchResult updateFiling(final String filingId, final Map<String, Object> patchMap);

}
