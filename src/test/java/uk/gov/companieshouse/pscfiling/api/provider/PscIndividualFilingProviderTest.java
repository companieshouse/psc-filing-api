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
import uk.gov.companieshouse.pscfiling.api.provider.impl.PscIndividualFilingProviderImpl;
import uk.gov.companieshouse.pscfiling.api.service.PscFilingService;

@ExtendWith(MockitoExtension.class)
class PscIndividualFilingProviderTest {

    private PscIndividualFilingProvider testProvider;
    private PscIndividualFiling filing;
    public static final String FILING_ID = "632c8e65105b1b4a9f0d1f5e";
    @Mock
    private PscFilingService filingService;
    @Mock
    private Logger logger;
    private PscCommunal corporate;


    @BeforeEach
    void setUp() {
        testProvider = new PscIndividualFilingProviderImpl(filingService, logger);
        filing = PscIndividualFiling.builder().id(FILING_ID).etag("etag").build();
        corporate = PscWithIdentificationFiling.builder().id(FILING_ID).etag("etag").build();
    }

    @Test
    void provide() {
        final var expected = new EntityRetrievalResult<>("etag", filing);
        when(filingService.get(FILING_ID)).thenReturn(Optional.of(filing));

        final var result = testProvider.provide(FILING_ID);

        assertThat(result.isSuccess(), is(true));
        assertThat(result, samePropertyValuesAs(expected));
    }

    @Test
    void provideWhenNotIndividualType() {
        when(filingService.get(FILING_ID)).thenReturn(Optional.of(corporate));

        final var result = testProvider.provide(FILING_ID);

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getFailureReason(), is(RetrievalFailureReason.FILING_NOT_FOUND));
    }

    @Test
    void provideWhenWrongFilingId() {
        final var individual = PscIndividualFiling.builder(filing).id(FILING_ID + "y").build();
        when(filingService.get(FILING_ID)).thenReturn(Optional.of(individual));

        final var result = testProvider.provide(FILING_ID);

        assertThat(result.isSuccess(), is(false));
        assertThat(result.getFailureReason(), is(RetrievalFailureReason.FILING_NOT_FOUND));


    }
}