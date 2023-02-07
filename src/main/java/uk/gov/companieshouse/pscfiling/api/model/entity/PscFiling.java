package uk.gov.companieshouse.pscfiling.api.model.entity;

public abstract class PscFiling <T> {

    private T filing = null;

    private Links links;

    public PscFiling() {

    }

    public PscFiling(T filing) {

        this.filing = filing;
    }

    public abstract T getFiling();

    public abstract Links getLinks();
}
