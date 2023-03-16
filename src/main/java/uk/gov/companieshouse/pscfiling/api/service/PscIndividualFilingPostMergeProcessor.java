package uk.gov.companieshouse.pscfiling.api.service;

import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.patch.service.PostMergeProcessor;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@Component
public class PscIndividualFilingPostMergeProcessor implements PostMergeProcessor<PscIndividualFiling> {
    private final Clock clock;

    @Autowired
    public PscIndividualFilingPostMergeProcessor(final Clock clock) {
        this.clock = clock;
    }

    @Override
    public void onMerge(final PscIndividualFiling filing) {
        filing.touch(clock.instant());
    }
}
