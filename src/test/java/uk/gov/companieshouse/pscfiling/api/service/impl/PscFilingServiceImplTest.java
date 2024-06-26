package uk.gov.companieshouse.pscfiling.api.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.repository.PscFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.repository.PscWithIdentificationFilingRepository;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

@ExtendWith(MockitoExtension.class)
class PscFilingServiceImplTest extends TestBaseService {
    private PscFilingService testService;
    @Mock
    private PscFilingRepository filingRepository;
    @Mock
    private PscIndividualFilingRepository individualFilingRepository;
    @Mock
    private PscWithIdentificationFilingRepository withIdentificationFilingRepository;
    @Mock
    private PscIndividualFiling filing;
    @Mock
    private PscWithIdentificationFiling identificationFiling;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Logger logger;

    @BeforeEach
    void setUp() {
        testService = new PscFilingServiceImpl(filingRepository, individualFilingRepository,
                withIdentificationFilingRepository);
    }

    @Test
    void saveStringStringIndividual() {
        testService.save(filing);

        verify(individualFilingRepository).save(filing);
    }

    @Test
    void saveStringIndividual() {
        testService.save(filing);

        verify(individualFilingRepository).save(filing);
    }


    @Test
    void saveWithIdentification() {
        testService.save(identificationFiling);

        verify(withIdentificationFilingRepository).save(identificationFiling);
    }
    @Test
    void getStringStringWhenFound() {
        final var filing = PscIndividualFiling.builder()
                .build();
        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        final var pscIndividualFiling = testService.get(FILING_ID);

        assertThat(pscIndividualFiling.isPresent(), is(true));
    }

    @Test
    void getStringWhenFound() {
        final var filing = PscIndividualFiling.builder()
                .build();
        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));

        final var pscIndividualFiling = testService.get(FILING_ID);

        assertThat(pscIndividualFiling.isPresent(), is(true));
    }

    @Test
    void getStringStringWhenNotFound() {
        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.empty());
        final var pscIndividualFiling = testService.get(FILING_ID);

        assertThat(pscIndividualFiling.isPresent(), is(false));
    }

    @Test
    void getStringWhenNotFound() {
        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.empty());
        final var pscIndividualFiling = testService.get(FILING_ID);

        assertThat(pscIndividualFiling.isPresent(), is(false));
    }

    @Test
    void requestMatchesResource() throws URISyntaxException {
        final var links = new Links(new URI("transactions/" + TRANS_ID), new URI("validation_status"));

        when(filing.getLinks()).thenReturn(links);
        when(request.getRequestURI()).thenReturn("transactions/" + TRANS_ID);

        assertThat(testService.requestMatchesResourceSelf(request, filing), is(true));
    }

    @Test
    void requestDoesNotMatchResource() throws URISyntaxException {
        final var links = new Links(new URI("transactions/" + TRANS_ID), new URI("validation_status"));

        when(filing.getLinks()).thenReturn(links);
        when(request.getRequestURI()).thenReturn("different");

        assertThat(testService.requestMatchesResourceSelf(request, filing), is(false));
    }

    @Test
    void requestMatchesResourceThrowsException() throws URISyntaxException {
        final var links = new Links(new URI("transactions/" + TRANS_ID), new URI("validation_status"));

        when(filing.getLinks()).thenReturn(links);
        when(request.getRequestURI()).thenReturn(":");

        final var thrown = assertThrows(URISyntaxException.class, () -> new URI(":"));
        assertThat(thrown.getMessage(), is("Expected scheme name at index 0: :"));
        assertThat(testService.requestMatchesResourceSelf(request, filing), is(false));
    }
}