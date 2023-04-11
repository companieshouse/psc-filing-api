package uk.gov.companieshouse.pscfiling.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.patch.model.EntityRetrievalResult;
import uk.gov.companieshouse.patch.model.ValidationResult;
import uk.gov.companieshouse.pscfiling.api.exception.MergePatchException;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.provider.PscIndividualFilingProvider;
import uk.gov.companieshouse.pscfiling.api.repository.PscIndividualFilingRepository;
import uk.gov.companieshouse.pscfiling.api.config.PatchServiceProperties;

@ExtendWith(MockitoExtension.class)
class PscIndividualFilingServiceImplTest {

    public static final String FILING_ID = "id";
    @Spy
    private PscIndividualFilingService testService;
    @Mock
    private PscIndividualFilingRepository filingRepository;
    @Mock
    private PatchServiceProperties patchServiceProperties;
    @Mock
    private PscIndividualFilingProvider pscIndividualFilingProvider;
    @Mock
    private PscIndividualFilingMergeProcessor mergeProcessor;
    @Mock
    private PscIndividualFilingPostMergeProcessor postMergeProcessor;
    @Mock
    private PscIndividualPatchValidator patchValidator;
    private PscIndividualFiling filing;

    @BeforeEach
    void setUp() {
        testService = new PscIndividualFilingServiceImpl(filingRepository, patchServiceProperties,
                pscIndividualFilingProvider, mergeProcessor, postMergeProcessor, patchValidator);
        filing = PscIndividualFiling.builder().etag("etag")
                .build();
    }

    @Test
    void createFiling() {
        testService.createFiling(filing);
        verify(filingRepository).save(filing);
    }

    @Test
    void getFiling() {
        testService.getFiling(FILING_ID);
        verify(filingRepository).findById(FILING_ID);
    }

    @Test
    void updateFiling() throws IOException {

        when(testService.getMaxRetries()).thenReturn(1);
        final EntityRetrievalResult<PscIndividualFiling> retrievalResult =
                new EntityRetrievalResult<>("etag", filing);
        when(pscIndividualFilingProvider.provide(FILING_ID)).thenReturn(retrievalResult);
        when(mergeProcessor.mergeEntity(filing, Collections.emptyMap())).thenReturn(filing);
        when(patchValidator.validate(filing)).thenReturn(new ValidationResult());

        var result = testService.updateFiling(FILING_ID, Collections.emptyMap());

        verify(postMergeProcessor).onMerge(filing);
        verify(filingRepository).save(filing);
        assertThat(result.isSuccess(), is(true));

    }

    @Test
    void updateFilingWithException() throws IOException {
        Map<String, Object> map = Collections.emptyMap();

        when(testService.getMaxRetries()).thenReturn(1);
        final EntityRetrievalResult<PscIndividualFiling> retrievalResult =
                new EntityRetrievalResult<>("etag", filing);
        when(pscIndividualFilingProvider.provide(FILING_ID)).thenReturn(retrievalResult);
        when(mergeProcessor.mergeEntity(filing, Collections.emptyMap())).thenThrow(
                new IOException("ioe"));

        final var exception = assertThrows(MergePatchException.class,
                () -> testService.updateFiling(FILING_ID, map));
        verifyNoInteractions(postMergeProcessor, filingRepository);
        assertThat(exception.getMessage(), is("Failed to merge patch request"));
        assertThat(exception.getCause().getMessage(), is("ioe"));

    }

    @Test
    void save() {

        var result = testService.save(filing, "etag");
        verify(filingRepository).save(
                argThat((PscIndividualFiling f) -> f.getEtag().equals("etag")));
        assertThat(result, is(1));

    }

    @Test
    void getMaxRetries() {

        when(patchServiceProperties.getMaxRetries()).thenReturn(123);

        assertThat(testService.getMaxRetries(), is(123));

    }
}