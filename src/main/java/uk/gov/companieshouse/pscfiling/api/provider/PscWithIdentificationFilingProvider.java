package uk.gov.companieshouse.pscfiling.api.provider;

import uk.gov.companieshouse.patch.model.EntityRetrievalResult;
import uk.gov.companieshouse.patch.provider.EntityProvider;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

public interface PscWithIdentificationFilingProvider extends EntityProvider<PscWithIdentificationFiling> {
    String getRequestId();

    void setRequestId(String requestId);

    @Override
    EntityRetrievalResult<PscWithIdentificationFiling> provide(String filingId);
}
