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
import uk.gov.companieshouse.pscfiling.api.utils.LogHelper;

@ExtendWith(MockitoExtension.class)
class PscFilingServiceImplTest {
    public static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    public static final String TRANS_ID = "12345-54321-76666";
    private PscFilingService testService;

    @Mock
    private PscFilingRepository repository;
    @Mock
    private PscIndividualFiling filing;
    @Mock
    private Logger logger;
    @Mock
    private LogHelper logHelper;

    @BeforeEach
    void setUp() {
        testService = new PscFilingServiceImpl(repository, logger);
    }

    @Test
    void save() {
        testService.save(filing, TRANS_ID);

        verify(repository).save(filing);
    }

    @Test
    void getWhenFound() {
        var filing = PscIndividualFiling.builder().build();
        when(repository.findById(FILING_ID)).thenReturn(Optional.of(filing));
        final var pscIndividualFiling = testService.get(FILING_ID, TRANS_ID);

        assertThat(pscIndividualFiling.isPresent(), is(true));
    }

    @Test
    void getWhenNotFound() {
        when(repository.findById(FILING_ID)).thenReturn(Optional.empty());
        final var pscIndividualFiling = testService.get(FILING_ID, TRANS_ID);

        assertThat(pscIndividualFiling.isPresent(), is(false));
    }

}