package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NaturesOfControlList extends ArrayList<String> {
    public NaturesOfControlList() {
    }

    public NaturesOfControlList(final Collection<? extends String> c) {
        super(c);
    }

    public List<String> getList() {
        return this;
    }
}
