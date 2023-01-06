package uk.gov.companieshouse.pscfiling.api.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilingDescription {
    @Value("${description.psc07}")
    private String psc07;

    // Spring no args constructor
    public FilingDescription() {
    }

    public void setPsc07(String psc07) {
        this.psc07 = psc07;
    }

    public String getPsc07() {
        return psc07;
    }
}
