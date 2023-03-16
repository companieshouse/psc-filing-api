package uk.gov.companieshouse.pscfiling.api.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.patch.model.EntityRetrievalResult;
import uk.gov.companieshouse.patch.provider.EntityProvider;
import uk.gov.companieshouse.pscfiling.api.error.RetrievalFailureReason;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

@Component
public class PscIndividualFilingProvider implements EntityProvider<PscIndividualFiling> {
    private final Logger logger;
    private final PscFilingService pscFilingService;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    private String requestId;

    @Autowired
    public PscIndividualFilingProvider(final PscFilingService pscFilingService, final Logger logger) {
        this.pscFilingService = pscFilingService;
        this.logger = logger;
    }

    @Override
    public EntityRetrievalResult<PscIndividualFiling> provide(final String filingId) {
        final var pscCommunal = pscFilingService.get(filingId);
        logger.debug("providing filing resource: " + filingId);

        return pscCommunal.filter(PscIndividualFiling.class::isInstance)
                .map(PscIndividualFiling.class::cast)
                .filter(f -> f.getId().equals(filingId))
                .map(f -> new EntityRetrievalResult<>(f.getEtag(), f))
                .orElseGet(
                        () -> new EntityRetrievalResult<>(RetrievalFailureReason.FILING_NOT_FOUND));
    }

}
