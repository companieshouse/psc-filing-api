package uk.gov.companieshouse.pscfiling.api.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualFilingDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

@Mapper(componentModel = "spring")
public interface PscIndividualFilingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "links", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "identification", ignore = true)
    @Mapping(target = "statementActionDate", ignore = true)
    @Mapping(target = "statementType", ignore = true)
    PscIndividualFiling map(PscIndividualFilingDto dto);

    PscIndividualFilingDto map(PscIndividualFiling pscIndividualFiling);

}
