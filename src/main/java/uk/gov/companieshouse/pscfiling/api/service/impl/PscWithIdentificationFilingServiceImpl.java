package uk.gov.companieshouse.pscfiling.api.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.patch.model.PatchResult;
import uk.gov.companieshouse.pscfiling.api.config.PatchServiceProperties;
import uk.gov.companieshouse.pscfiling.api.exception.MergePatchException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.provider.PscWithIdentificationFilingProvider;
import uk.gov.companieshouse.pscfiling.api.repository.PscWithIdentificationFilingRepository;
import uk.gov.companieshouse.pscfiling.api.service.PscWithIdentificationFilingMergeProcessor;
import uk.gov.companieshouse.pscfiling.api.service.PscWithIdentificationFilingPostMergeProcessor;
import uk.gov.companieshouse.pscfiling.api.service.PscWithIdentificationFilingService;
import uk.gov.companieshouse.pscfiling.api.service.PscWithIdentificationPatchValidator;

@Service
public class PscWithIdentificationFilingServiceImpl implements PscWithIdentificationFilingService {
    private final PscWithIdentificationFilingRepository filingRepository;
    private final PatchServiceProperties patchServiceProperties;
    private final PscWithIdentificationFilingProvider pscWithIdentificationFilingProvider;
    private final PscWithIdentificationFilingMergeProcessor mergeProcessor;
    private final PscWithIdentificationFilingPostMergeProcessor postMergeProcessor;
    private final PscWithIdentificationPatchValidator pscWithIdentificationPatchValidator;

    @Autowired
    public PscWithIdentificationFilingServiceImpl(final PscWithIdentificationFilingRepository filingRepository,
            final PatchServiceProperties patchServiceProperties,
            final PscWithIdentificationFilingProvider pscWithIdentificationFilingProvider,
            final PscWithIdentificationFilingMergeProcessor mergeProcessor,
            final PscWithIdentificationFilingPostMergeProcessor postMergeProcessor,
            final PscWithIdentificationPatchValidator pscWithIdentificationPatchValidator) {
        this.filingRepository = filingRepository;
        this.patchServiceProperties = patchServiceProperties;
        this.pscWithIdentificationFilingProvider = pscWithIdentificationFilingProvider;
        this.mergeProcessor = mergeProcessor;
        this.postMergeProcessor = postMergeProcessor;
        this.pscWithIdentificationPatchValidator = pscWithIdentificationPatchValidator;
    }

    @Override
    public PscWithIdentificationFiling save(final PscWithIdentificationFiling filing) {
        return filingRepository.save(filing);
    }

    @Override
    public Optional<PscWithIdentificationFiling> get(final String filingId) {
        return filingRepository.findById(filingId);
    }

    @Override
    public PatchResult patch(final String filingId, final Map<String, Object> patchMap) {
        final PatchResult patchResult;

        try {
            patchResult =
                    patchEntity(filingId, pscWithIdentificationFilingProvider, patchMap, mergeProcessor,
                            postMergeProcessor, pscWithIdentificationPatchValidator);
        }
        catch (final IOException e) {
            throw new MergePatchException(e);
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
