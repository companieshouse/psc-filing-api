package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.time.Instant;

@FunctionalInterface
public interface Touchable {
    void touch(Instant instant);
}
