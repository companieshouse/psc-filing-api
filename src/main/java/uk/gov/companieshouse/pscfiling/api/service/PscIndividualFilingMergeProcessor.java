package uk.gov.companieshouse.pscfiling.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.patch.service.MergeProcessor;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@Component
public class PscIndividualFilingMergeProcessor implements MergeProcessor<PscIndividualFiling> {
    private final ObjectMapper patchObjectMapper;

    @Autowired
    public PscIndividualFilingMergeProcessor(
            @Qualifier("patchObjectMapper") final ObjectMapper patchObjectMapper) {
        this.patchObjectMapper = patchObjectMapper;
    }

    @Override
    public PscIndividualFiling mergeEntity(final PscIndividualFiling target,
            final Map<String, Object> patchMap) throws IOException {
        final var json = patchObjectMapper.writeValueAsString(patchMap);
        final var patched = patchObjectMapper.readerForUpdating(target).readValue(json);

        return (PscIndividualFiling) patched;
    }

}
