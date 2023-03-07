package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
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
    private Logger logger;
    @Mock
    private LogHelper logHelper;

    @BeforeEach
    void setUp() {
        testService = new PscFilingServiceImpl(filingRepository, individualFilingRepository,
                withIdentificationFilingRepository, logger);
    }

    @Test
    void save() {
        testService.save(filing, TRANS_ID);

        verify(individualFilingRepository).save(filing);
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

}