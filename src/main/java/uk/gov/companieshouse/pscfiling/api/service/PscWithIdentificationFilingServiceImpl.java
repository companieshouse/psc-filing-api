package uk.gov.companieshouse.pscfiling.api.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.pscfiling.api.exception.MergePatchException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.provider.PscWithIdentificationFilingProvider;
import uk.gov.companieshouse.pscfiling.api.repository.PscWithIdentificationFilingRepository;
import uk.gov.companieshouse.pscfiling.api.utils.PatchServiceProperties;

@Service
public class PscWithIdentificationFilingServiceImpl implements PscWithIdentificationFilingService {
    private final PscWithIdentificationFilingRepository filingRepository;
    private final PatchServiceProperties patchServiceProperties;
    private PscWithIdentificationFilingProvider PscWithIdentificationFilingProvider;
    private PscWithIdentificationFilingMergeProcessor mergeProcessor;
    private PscWithIdentificationFilingPostMergeProcessor postMergeProcessor;
    private PscWithIdentificationPatchValidator PscWithIdentificationPatchValidator;

    @Autowired
    public PscWithIdentificationFilingServiceImpl(final PscWithIdentificationFilingRepository filingRepository,
            final PatchServiceProperties patchServiceProperties,
            final PscWithIdentificationFilingProvider PscWithIdentificationFilingProvider,
            final PscWithIdentificationFilingMergeProcessor mergeProcessor,
            final PscWithIdentificationFilingPostMergeProcessor postMergeProcessor,
            final PscWithIdentificationPatchValidator PscWithIdentificationPatchValidator) {
        this.filingRepository = filingRepository;
        this.patchServiceProperties = patchServiceProperties;
        this.PscWithIdentificationFilingProvider = PscWithIdentificationFilingProvider;
        this.mergeProcessor = mergeProcessor;
        this.postMergeProcessor = postMergeProcessor;
        this.PscWithIdentificationPatchValidator = PscWithIdentificationPatchValidator;
    }

    @Override
    public PscWithIdentificationFiling createFiling(final PscWithIdentificationFiling filing) {
        return filingRepository.save(filing);
    }

    @Override
    public Optional<PscWithIdentificationFiling> getFiling(final String filingId) {
        return filingRepository.findById(filingId);
    }

    @Override
    public PatchResult updateFiling(final String filingId, final Map<String, Object> patchMap) {
        final PatchResult patchResult;

        try {
            patchResult =
                    patchEntity(filingId, PscWithIdentificationFilingProvider, patchMap, mergeProcessor,
                            postMergeProcessor, PscWithIdentificationPatchValidator);
        }
        catch (final IOException e) {
            throw new MergePatchException("Failed to merge patch request", e);
        }

        return patchResult;
    }

    @Override
    public int save(final PscWithIdentificationFiling filing, final String version) {
        filingRepository.save(PscWithIdentificationFiling.builder(filing).etag(version)
                .build());

        return 1;
    }

    @Override
    public int getMaxRetries() {
        return patchServiceProperties.getMaxRetries();
    }
}
