package uk.gov.companieshouse.pscfiling.api.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.patch.model.EntityRetrievalResult;
import uk.gov.companieshouse.pscfiling.api.error.RetrievalFailureReason;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

@ExtendWith(MockitoExtension.class)
class PscWithIdentificationFilingProviderTest {

    private PscWithIdentificationFilingProvider testProvider;
    private PscWithIdentificationFiling filing;
    public static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    @Mock
    private PscFilingService filingService;
    @Mock
    private Logger logger;
    private PscCommunal individual;


    @BeforeEach
    void setUp() {
        testProvider = new PscWithIdentificationFilingProviderImpl(filingService, logger);
        filing = PscWithIdentificationFiling.builder().id(FILING_ID).etag("etag").build();
        individual = PscIndividualFiling.builder().id(FILING_ID).etag("etag").build();
    }

    @Test
    void setGetRequestId() {
        testProvider.setRequestId("test_id");
        assertThat(testProvider.getRequestId(), is("test_id"));
    }

    @Test
    void provide() {
        var expected = new EntityRetrievalResult<>("etag", filing);
        when(filingService.get(FILING_ID)).thenReturn(Optional.of(filing));

        var result = testProvider.provide(FILING_ID);

        assertThat(result.isSuccess(), is(true));
        assertThat(result, samePropertyValuesAs(expected));
    }

    @Test
    void provideWhenNotWithIdentificationType() {
        when(filingService.get(FILING_ID)).thenReturn(Optional.of(individual));

        var result = testProvider.provide(FILING_ID);

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getFailureReason(), is(RetrievalFailureReason.FILING_NOT_FOUND));
    }

    @Test
    void provideWhenWrongFilingId() {
        final var individual = PscWithIdentificationFiling.builder(filing).id(FILING_ID + "y").build();
        when(filingService.get(FILING_ID)).thenReturn(Optional.of(individual));

        var result = testProvider.provide(FILING_ID);

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getFailureReason(), is(RetrievalFailureReason.FILING_NOT_FOUND));


    }
}