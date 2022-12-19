package uk.gov.companieshouse.pscfiling.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.dto.FilingDataDto;

@Mapper(componentModel = "spring")//, uses = NameElementsMapper.class)
public interface PscIndividualMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "links", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statementActionDate", ignore = true)
    @Mapping(target = "statementType", ignore = true)
    PscIndividualFiling map(PscIndividualDto dto);

    PscIndividualDto map(PscIndividualFiling filing);

    @Mapping(target = "firstName", source = "nameElements.forename")
    @Mapping(target = "otherForenames", source = "nameElements.otherForenames")
    @Mapping(target = "lastName", source = "nameElements.surname")
    FilingDataDto mapFiling(final PscIndividualFiling entity);

}
