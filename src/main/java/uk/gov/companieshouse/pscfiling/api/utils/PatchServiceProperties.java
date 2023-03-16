package uk.gov.companieshouse.pscfiling.api.utils;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "patch")
public class PatchServiceProperties {
    @NotNull
    private Integer maxRetries;

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(final Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
}
