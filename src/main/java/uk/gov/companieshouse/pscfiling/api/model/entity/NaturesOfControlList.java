package uk.gov.companieshouse.pscfiling.api.model.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An entity model to store a list of natures of control.
 */
public class NaturesOfControlList extends ArrayList<String> {
    public NaturesOfControlList() {
    }

    public NaturesOfControlList(final Collection<String> c) {
        super(c);
    }

    /**
     * @return a list of natures of control.
     */
    public List<String> getList() {
        return this;
    }
}
