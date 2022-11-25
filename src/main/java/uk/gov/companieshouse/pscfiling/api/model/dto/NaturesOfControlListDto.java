package uk.gov.companieshouse.pscfiling.api.model.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NaturesOfControlListDto extends ArrayList<String> {
    public NaturesOfControlListDto() {
    }

    public NaturesOfControlListDto(final Collection<? extends String> c) {
        super(c);
    }

    public List<String> getList() {
        return this;
    }
}
