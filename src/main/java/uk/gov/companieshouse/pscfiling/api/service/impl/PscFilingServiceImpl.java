package uk.gov.companieshouse.pscfiling.api.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscWithIdentificationFilingRepository;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

/**
 * Store/retrieve a PSC Filing entities using the persistence layer.
 */
@Service
public class PscFilingServiceImpl implements PscFilingService {
    private final PscFilingRepository filingRepository;
    private final PscIndividualFilingRepository individualFilingRepository;
    private final PscWithIdentificationFilingRepository withIdentificationFilingRepository;

    public PscFilingServiceImpl(final PscFilingRepository filingRepository, final PscIndividualFilingRepository individualFilingRepository,
            final PscWithIdentificationFilingRepository withIdentificationFilingRepository) {
        this.filingRepository = filingRepository;
        this.individualFilingRepository = individualFilingRepository;
        this.withIdentificationFilingRepository = withIdentificationFilingRepository;
    }

    /**
     * Retrieve a stored PSCFiling entity by Filing ID.
     *
     * @param pscFilingId   the Filing ID
     * @return the stored entity if found
     */
    @Override
    public Optional<PscCommunal> get(final String pscFilingId) {
        return filingRepository.findById(pscFilingId);
    }

    /**
     * Store a PSCIndividualFiling entity in persistence layer.
     *
     * @param filing        the PSCIndividualFiling entity to store
     * @return the stored entity
     */
    @Override
    public PscIndividualFiling save(final PscIndividualFiling filing) {
        return individualFilingRepository.save(filing);
    }

    /**
     * Store a PSCWithIdentificationFiling entity in persistence layer.
     *
     * @param filing        the PSCWithIdentificationFiling entity to store
     * @return the stored entity
     */
    @Override
    public PscWithIdentificationFiling save(final PscWithIdentificationFiling filing) {
        return withIdentificationFilingRepository.save(filing);
    }

    @Override
    public boolean requestMatchesResourceSelf(final HttpServletRequest request, final PscCommunal pscFiling) {
        final var selfLinkUri = pscFiling.getLinks().self();
        final URI requestUri;
        try {
            requestUri = new URI(request.getRequestURI());
        } catch (final URISyntaxException e) {
            return false;
        }
        return selfLinkUri.equals(requestUri.normalize());
    }
}
