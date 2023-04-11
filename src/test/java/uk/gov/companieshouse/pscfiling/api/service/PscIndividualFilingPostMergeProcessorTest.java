package uk.gov.companieshouse.pscfiling.api.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@ExtendWith(MockitoExtension.class)
class PscIndividualFilingPostMergeProcessorTest {
    private static final Instant FIRST_INSTANT = Instant.parse("2022-10-15T09:44:08.108Z");

    private PscIndividualFilingPostMergeProcessor testProcessor;
    @Mock
    private Clock clock;
    @Mock
    private PscIndividualFiling filing;

    @BeforeEach
    void setUp() {
        testProcessor = new PscIndividualFilingPostMergeProcessor(clock);
    }

    @Test
    void onMerge() {
        when(clock.instant()).thenReturn(FIRST_INSTANT);

        testProcessor.onMerge(filing);

        verify(filing).touch(FIRST_INSTANT);

    }
}