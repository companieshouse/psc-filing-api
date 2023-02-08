package uk.gov.companieshouse.pscfiling.api.mapper;

import uk.gov.companieshouse.pscfiling.api.model.dto.PscDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscFiling;

public interface PscMapper {

    public PscDto map(PscFiling filing);
}