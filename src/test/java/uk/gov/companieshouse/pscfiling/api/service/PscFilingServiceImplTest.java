package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@ExtendWith(MockitoExtension.class)
class PscFilingServiceImplTest extends BaseServiceTestClass {
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
    @Mock
    private LogHelper logHelper;

    @BeforeEach
    void setUp() {
        testService = new PscFilingServiceImpl(filingRepository, individualFilingRepository,
                withIdentificationFilingRepository, logger);
    }

    @Test
    void saveIndividual() {
        testService.save(filing, TRANS_ID);

        verify(individualFilingRepository).save(filing);
    }

    @Test
    void saveWithIdentification() {
        testService.save(identificationFiling, TRANS_ID);

        verify(withIdentificationFilingRepository).save(identificationFiling);
    }
    @Test
    void getWhenFound() {
        final var filing = PscIndividualFiling.builder()
                .build();
        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        final var pscIndividualFiling = testService.get(FILING_ID, TRANS_ID);

        assertThat(pscIndividualFiling.isPresent(), is(true));
    }

    @Test
    void getWhenNotFound() {
        when(filingRepository.findById(FILING_ID)).thenReturn(Optional.empty());
        final var pscIndividualFiling = testService.get(FILING_ID, TRANS_ID);

        assertThat(pscIndividualFiling.isPresent(), is(false));
    }

    @Test
    void requestMatchesResource() throws URISyntaxException {
        var links = new Links(new URI("transactions/" + TRANS_ID), new URI("validation_status"));

        when(filing.getLinks()).thenReturn(links);
        when(request.getRequestURI()).thenReturn("transactions/" + TRANS_ID);

        assertThat(testService.requestMatchesResource(request, filing), is(true));
    }

    @Test
    void requestDoesNotMatchResource() throws URISyntaxException {
        var links = new Links(new URI("transactions/" + TRANS_ID), new URI("validation_status"));

        when(filing.getLinks()).thenReturn(links);
        when(request.getRequestURI()).thenReturn("different");

        assertThat(testService.requestMatchesResource(request, filing), is(false));
    }

    @Test
    void requestMatchesResourceThrowsException() throws URISyntaxException {
        var links = new Links(new URI("transactions/" + TRANS_ID), new URI("validation_status"));

        when(filing.getLinks()).thenReturn(links);
        when(request.getRequestURI()).thenReturn(":");

        var thrown = assertThrows(URISyntaxException.class, () -> new URI(":"));
        assertThat(thrown.getMessage(), is("Expected scheme name at index 0: :"));
        assertThat(testService.requestMatchesResource(request, filing), is(false));
    }
}