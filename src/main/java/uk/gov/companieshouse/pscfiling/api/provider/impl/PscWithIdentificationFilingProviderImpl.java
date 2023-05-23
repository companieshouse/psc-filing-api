package uk.gov.companieshouse.pscfiling.api.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.patch.model.EntityRetrievalResult;
import uk.gov.companieshouse.pscfiling.api.error.RetrievalFailureReason;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.provider.PscWithIdentificationFilingProvider;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

@Component
public class PscWithIdentificationFilingProviderImpl implements
        PscWithIdentificationFilingProvider {
    private final Logger logger;
    private final PscFilingService pscFilingService;

    @Autowired
    public PscWithIdentificationFilingProviderImpl(final PscFilingService pscFilingService, final Logger logger) {
        this.pscFilingService = pscFilingService;
        this.logger = logger;
    }

    @Override
    public EntityRetrievalResult<PscWithIdentificationFiling> provide(final String filingId) {
        final var pscCommunal = pscFilingService.get(filingId);
        logger.debug("providing filing resource: " + filingId);

        return pscCommunal.filter(PscWithIdentificationFiling.class::isInstance)
                .map(PscWithIdentificationFiling.class::cast)
                .filter(f -> f.getId().equals(filingId))
                .map(f -> new EntityRetrievalResult<>(f.getEtag(), f))
                .orElseGet(
                        () -> new EntityRetrievalResult<>(RetrievalFailureReason.FILING_NOT_FOUND));
    }

}
