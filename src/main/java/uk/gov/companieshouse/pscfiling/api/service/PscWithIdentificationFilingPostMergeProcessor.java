package uk.gov.companieshouse.pscfiling.api.service;

import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.patch.service.PostMergeProcessor;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Component
public class PscWithIdentificationFilingPostMergeProcessor implements PostMergeProcessor<PscWithIdentificationFiling> {
    private final Clock clock;

    @Autowired
    public PscWithIdentificationFilingPostMergeProcessor(final Clock clock) {
        this.clock = clock;
    }

    @Override
    public void onMerge(final PscWithIdentificationFiling filing) {
        filing.touch(clock.instant());
    }
}
