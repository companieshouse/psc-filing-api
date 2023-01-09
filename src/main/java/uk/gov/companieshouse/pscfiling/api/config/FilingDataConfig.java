package uk.gov.companieshouse.pscfiling.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilingDataConfig {
    @Value("${filing.data.description.psc07}")
    private String psc07Description;

    public void setPsc07Description(String psc07Description) {
        this.psc07Description = psc07Description;
    }

    public String getPsc07Description() {
        return psc07Description;
    }
}
