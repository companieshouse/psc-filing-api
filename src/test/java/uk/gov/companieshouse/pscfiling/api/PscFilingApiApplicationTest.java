package uk.gov.companieshouse.pscfiling.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.pscfiling.api.controller.PscFilingController;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscFilingWithIdentification;

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

