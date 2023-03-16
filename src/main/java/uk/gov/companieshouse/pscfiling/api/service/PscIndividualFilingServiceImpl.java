package uk.gov.companieshouse.pscfiling.api.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.provider.PscIndividualFilingProvider;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.utils.PatchServiceProperties;

@Service
public class PscIndividualFilingServiceImpl implements PscIndividualFilingService {
    private final PscIndividualFilingRepository filingRepository;
    private final PatchServiceProperties patchServiceProperties;
    private PscIndividualFilingProvider pscIndividualFilingProvider;
    private PscIndividualFilingMergeProcessor mergeProcessor;
    private PscIndividualFilingPostMergeProcessor postMergeProcessor;

    @Autowired
    public PscIndividualFilingServiceImpl(final PscIndividualFilingRepository filingRepository,
            final PatchServiceProperties patchServiceProperties,
            final PscIndividualFilingProvider pscIndividualFilingProvider,
            final PscIndividualFilingMergeProcessor mergeProcessor,
            final PscIndividualFilingPostMergeProcessor postMergeProcessor) {
        this.filingRepository = filingRepository;
        this.patchServiceProperties = patchServiceProperties;
        this.pscIndividualFilingProvider = pscIndividualFilingProvider;
        this.mergeProcessor = mergeProcessor;
        this.postMergeProcessor = postMergeProcessor;
    }

    @Override
    public PscIndividualFiling createFiling(final PscIndividualFiling filing) {
        return filingRepository.save(filing);
    }

    @Override
    public Optional<PscIndividualFiling> getFiling(final String filingId) {
        return filingRepository.findById(filingId);
    }

    @Override
    public PatchResult updateFiling(final String filingId, final Map<String, Object> patchMap) {
        final PatchResult patchResult;

        try {
            patchResult =
                    patchEntity(filingId, pscIndividualFilingProvider, patchMap, mergeProcessor,
                            postMergeProcessor);
        }
        catch (IOException e) {
            throw new RuntimeException(e);  // TODO InvalidPatchException?
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
