package uk.gov.companieshouse.pscfiling.api.provider;

import uk.gov.companieshouse.patch.model.EntityRetrievalResult;
import uk.gov.companieshouse.patch.provider.EntityProvider;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

public interface PscIndividualFilingProvider
        extends EntityProvider<PscIndividualFiling>, RequestIdentifiable {

    @Override
    EntityRetrievalResult<PscIndividualFiling> provide(String filingId);
}
