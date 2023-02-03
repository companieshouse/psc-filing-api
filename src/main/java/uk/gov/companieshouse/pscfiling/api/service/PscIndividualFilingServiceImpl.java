package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

/**
 * Store/retrieves a PSC Individual Filing entities using the persistence layer.
 */
@Service
public class PscIndividualFilingServiceImpl implements PscIndividualFilingService {
    private final PscIndividualFilingRepository repository;
    private final Logger logger;

    public PscIndividualFilingServiceImpl(final PscIndividualFilingRepository repository, Logger logger) {
        this.repository = repository;
        this.logger = logger;
    }

    /**
     * Store a PSCIndividualFiling entity in persistence layer.
     *
     * @param filing        the PSCIndividualFiling entity to store
     * @param transactionId the associated Transaction ID
     * @return the stored entity
     */
    @Override
    public PscIndividualFiling save(final PscIndividualFiling filing, final String transactionId) {
        final var logMap = LogHelper.createLogMap(transactionId, filing.getId());

        logger.debugContext(transactionId, "saving PSC filing", logMap);

        return repository.save(filing);
    }

    /**
     * Retrieve a stored PSCIndividualFiling entity by Filing ID.
     *
     * @param pscFilingId the Filing ID
     * @param transactionId   the associated Transaction ID
     * @return the stored entity if found
     */
    @Override
    public Optional<PscIndividualFiling> get(String pscFilingId, String transactionId) {
        final var logMap = LogHelper.createLogMap(transactionId, pscFilingId);

        logger.debugContext(transactionId, "getting PSC filing", logMap);

        return repository.findById(pscFilingId);
    }

}
