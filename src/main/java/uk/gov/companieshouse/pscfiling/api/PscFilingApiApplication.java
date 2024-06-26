package uk.gov.companieshouse.pscfiling.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PscFilingApiApplication {

    public static final String APPLICATION_NAME_SPACE = "psc-filing-api";

    public static void main(final String[] args) {
        SpringApplication.run(PscFilingApiApplication.class, args);
    }

}
