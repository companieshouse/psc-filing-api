package uk.gov.companieshouse.pscfiling.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.pscfiling.api.controller.PscIndividualFilingController;

@Tag("app")
@SpringBootTest
class PscFilingApiApplicationTest {
    @Autowired
    private PscIndividualFilingController pscIndividualFilingController;

    @Test
    void contextLoads() {
        assertThat(pscIndividualFilingController, is(not(nullValue())));
    }

}

