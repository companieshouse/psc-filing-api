package uk.gov.companieshouse.pscfiling.api.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@ExtendWith(MockitoExtension.class)
class PscIndividualFilingMergeProcessorTest {

    private PscIndividualFilingMergeProcessor testProcessor;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ObjectMapper patchObjectMapper;

    @BeforeEach
    void setUp() {
        testProcessor = new PscIndividualFilingMergeProcessor(patchObjectMapper);
    }

    @Test
    void mergeEntity() throws IOException {

        final Map<String, Object> patchMap = Map.of("key", "value");
        final PscIndividualFiling target = PscIndividualFiling.builder()
                .build();
        when(patchObjectMapper.writeValueAsString(patchMap)).thenReturn("json");

        testProcessor.mergeEntity(target, patchMap);

        verify(patchObjectMapper).writeValueAsString(patchMap);
        verify(patchObjectMapper.readerForUpdating(target)).readValue("json");
    }
}