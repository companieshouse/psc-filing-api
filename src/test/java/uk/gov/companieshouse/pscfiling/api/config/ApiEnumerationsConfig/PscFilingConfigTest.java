package uk.gov.companieshouse.pscfiling.api.config.ApiEnumerationsConfig;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
class PscFilingConfigTest {
    @Autowired
    @Qualifier(value = "validation")
    private Map<String, String> validation;

    @Autowired
    @Qualifier(value = "company")
    public Map<String, List<String>> company;

    @Test
    public void pscFiling() {
        assertThat(validation.get("ceased-date-before-notified-date"),
                is("Ceased date must be on or after the date the PSC was added"));

        assertThat(company.get("status-not-allowed"), contains("dissolved", "converted-closed"));
    }
}

