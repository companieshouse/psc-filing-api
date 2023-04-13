package uk.gov.companieshouse.pscfiling.api.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualPatchValidator;
import uk.gov.companieshouse.pscfiling.api.service.impl.PscIndividualPatchValidatorImpl;

@ExtendWith(MockitoExtension.class)
class PscIndividualPatchValidatorImplTest {

    private static final Instant FIRST_INSTANT = Instant.parse("2022-10-15T09:44:08.108Z");
    private PscIndividualPatchValidator testValidator;
    @Mock
    private Clock clock;

    @BeforeEach
    void setUp() {
        testValidator = new PscIndividualPatchValidatorImpl(clock);
        when(clock.instant()).thenReturn(FIRST_INSTANT);
    }

    @Test
    void validate() {
        var filing = PscIndividualFiling.builder().ceasedOn(LocalDate.of(2022, 10, 15))
                .build();
        final var result = testValidator.validate(filing);

        assertThat(result.isSuccess(), is(true));
    }

    //TODO test when validate() fails
}