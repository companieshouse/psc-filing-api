package uk.gov.companieshouse.pscfiling.api.mapper;

import org.mapstruct.Mapping;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

public interface PscWithIdentificationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "links", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statementActionDate", ignore = true)
    @Mapping(target = "statementType", ignore = true)
    PscWithIdentificationFiling map(PscWithIdentificationDto dto);

    PscWithIdentificationDto map(PscWithIdentificationFiling filing);

    @Mapping(target = "countryRegistered", source = "identification.countryRegistered")
    @Mapping(target = "placeRegistered", source = "identification.placeRegistered")
    @Mapping(target = "registrationNumber", source = "identification.registrationNumber")
    @Mapping(target = "legalAuthority", source = "identification.legalAuthority")
    @Mapping(target = "legalForm", source = "identification.legalForm")
    WithIdentificationFilingDataDto mapFiling(final PscWithIdentificationFiling entity);

}
