package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

public class PscWithIdentificationFilingServiceImpl implements PscWithIdentificationFilingService {

    private final PscIndividualFilingRepository repository;
    private final Logger logger;

    public PscWithIdentificationFilingServiceImpl(final PscIndividualFilingRepository repository,
            Logger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public PscWithIdentificationFiling save(final PscWithIdentificationFiling filing,
            final String transactionId) {
        final var logMap = LogHelper.createLogMap(transactionId, filing.getId());

        logger.debugContext(transactionId, "saving PSC filing", logMap);

        return repository.save(filing);
    }

    @Override
    public Optional<PscWithIdentificationFiling> get(final String pscFilingId,
            final String transactionId) {
        return Optional.empty();
    }
}