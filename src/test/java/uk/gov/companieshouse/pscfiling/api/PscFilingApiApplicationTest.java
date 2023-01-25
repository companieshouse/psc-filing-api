package uk.gov.companieshouse.pscfiling.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.pscfiling.api.controller.PscFilingController;

@Tag("app")
@SpringBootTest
class PscFilingApiApplicationTest {
    @Autowired
    private PscFilingController pscFilingController;

    @Test
    void contextLoads() {
        assertThat(pscFilingController, is(not(nullValue())));
    }

}

