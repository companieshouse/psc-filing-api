package uk.gov.companieshouse.pscfiling.api.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A dto to store a list of natures of control.
 */
public class NaturesOfControlListDto extends ArrayList<String> {

    public NaturesOfControlListDto(final Collection<String> c) {
        super(c);
    }

    /**
     * @return a list of natures of control.
     */
    public List<String> getList() {
        return this;
    }
}
