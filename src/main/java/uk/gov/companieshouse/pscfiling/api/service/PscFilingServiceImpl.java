package uk.gov.companieshouse.pscfiling.api.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscWithIdentificationFilingRepository;
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

/**
 * Store/retrieves a PSC Individual Filing entities using the persistence layer.
 */
@Service
public class PscFilingServiceImpl implements PscFilingService {
    private final PscFilingRepository filingRepository;
    private final PscIndividualFilingRepository individualFilingRepository;
    private final PscWithIdentificationFilingRepository withIdentificationFilingRepository;
    private final Logger logger;

    public PscFilingServiceImpl(final PscFilingRepository filingRepository, final PscIndividualFilingRepository individualFilingRepository,
            final PscWithIdentificationFilingRepository withIdentificationFilingRepository, final Logger logger) {
        this.filingRepository = filingRepository;
        this.individualFilingRepository = individualFilingRepository;
        this.withIdentificationFilingRepository = withIdentificationFilingRepository;
        this.logger = logger;
    }

    /**
     * Retrieve a stored PSCFiling entity by Filing ID.
     *
     * @param pscFilingId   the Filing ID
     * @param transactionId the associated Transaction ID
     * @return the stored entity if found
     */
    @Override
    public Optional<PscCommunal> get(final String pscFilingId, final String transactionId) {
        final var logMap = LogHelper.createLogMap(transactionId, pscFilingId);

        logger.debugContext(transactionId, "getting PSC filing", logMap);

        return filingRepository.findById(pscFilingId);
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

        return individualFilingRepository.save(filing);
    }

    /**
     * Store a PSCWithIdentificationFiling entity in persistence layer.
     *
     * @param filing        the PSCWithIdentificationFiling entity to store
     * @param transactionId the associated Transaction ID
     * @return the stored entity
     */
    @Override
    public PscWithIdentificationFiling save(final PscWithIdentificationFiling filing,
                                            final String transactionId) {
        final var logMap = LogHelper.createLogMap(transactionId, filing.getId());

        logger.debugContext(transactionId, "saving PSC filing", logMap);

        return withIdentificationFilingRepository.save(filing);
    }

}
