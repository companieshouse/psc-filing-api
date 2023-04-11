package uk.gov.companieshouse.pscfiling.api.provider;

public interface RequestIdentifiable {
    String getRequestId();

    void setRequestId(String requestId);
}
