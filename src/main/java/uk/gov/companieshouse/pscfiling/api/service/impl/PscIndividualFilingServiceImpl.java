package uk.gov.companieshouse.pscfiling.api.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.pscfiling.api.config.PatchServiceProperties;
import uk.gov.companieshouse.pscfiling.api.exception.MergePatchException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.provider.PscIndividualFilingProvider;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingMergeProcessor;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingPostMergeProcessor;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualPatchValidator;

@Service
public class PscIndividualFilingServiceImpl implements PscIndividualFilingService {
    private final PscIndividualFilingRepository filingRepository;
    private final PatchServiceProperties patchServiceProperties;
    private final PscIndividualFilingProvider pscIndividualFilingProvider;
    private final PscIndividualFilingMergeProcessor mergeProcessor;
    private final PscIndividualFilingPostMergeProcessor postMergeProcessor;
    private final PscIndividualPatchValidator pscIndividualPatchValidator;

    @Autowired
    public PscIndividualFilingServiceImpl(final PscIndividualFilingRepository filingRepository,
            final PatchServiceProperties patchServiceProperties,
            final PscIndividualFilingProvider pscIndividualFilingProvider,
            final PscIndividualFilingMergeProcessor mergeProcessor,
            final PscIndividualFilingPostMergeProcessor postMergeProcessor,
            final PscIndividualPatchValidator pscIndividualPatchValidator) {
        this.filingRepository = filingRepository;
        this.patchServiceProperties = patchServiceProperties;
        this.pscIndividualFilingProvider = pscIndividualFilingProvider;
        this.mergeProcessor = mergeProcessor;
        this.postMergeProcessor = postMergeProcessor;
        this.pscIndividualPatchValidator = pscIndividualPatchValidator;
    }

    @Override
    public PscIndividualFiling save(final PscIndividualFiling filing) {
        return filingRepository.save(filing);
    }

    @Override
    public Optional<PscIndividualFiling> get(final String filingId) {
        return filingRepository.findById(filingId);
    }

    @Override
    public PatchResult patch(final String filingId, final Map<String, Object> patchMap) {
        final PatchResult patchResult;

        try {
            patchResult =
                    patchEntity(filingId, pscIndividualFilingProvider, patchMap, mergeProcessor,
                            postMergeProcessor, pscIndividualPatchValidator);
        } catch (final IOException e) {
            throw new MergePatchException(e);
        }

        return patchResult;
    }

    @Override
    public int save(final PscIndividualFiling filing, final String version) {
        filingRepository.save(PscIndividualFiling.builder(filing).etag(version)
                .build());

        return 1;
    }

    @Override
    public int getMaxRetries() {
        return patchServiceProperties.getMaxRetries();
    }
}
