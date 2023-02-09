package uk.gov.companieshouse.pscfiling.api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import uk.gov.companieshouse.pscfiling.api.model.dto.IndividualFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Mapper(componentModel = "spring")//, uses = NameElementsMapper.class)
public interface PscMapper {

    @SubclassMapping(source = PscIndividualDto.class, target = PscIndividualFiling.class)
    @SubclassMapping(source = PscWithIdentificationDto.class, target = PscWithIdentificationFiling.class)
    PscFiling map(PscDto dto);

    @SubclassMapping(source = PscIndividualFiling.class, target = PscIndividualDto.class)
    @SubclassMapping(source = PscWithIdentificationFiling.class, target = PscWithIdentificationDto.class)
    PscDto map(PscFiling dto);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "kind", ignore = true)
//    @Mapping(target = "etag", ignore = true)
//    @Mapping(target = "links", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "statementActionDate", ignore = true)
//    @Mapping(target = "statementType", ignore = true)
//    PscIndividualFiling map(PscIndividualDto dto);

//    PscIndividualDto map(PscIndividualFiling filing);

//    @Mapping(target = "title", source = "nameElements.title")
//    @Mapping(target = "firstName", source = "nameElements.forename")
//    @Mapping(target = "otherForenames", source = "nameElements.otherForenames")
//    @Mapping(target = "lastName", source = "nameElements.surname")
//    IndividualFilingDataDto mapFiling(final PscIndividualFiling entity);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    default String isoDateOfBirth(Date3Tuple tuple) {
        if (tuple == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE.format(
            LocalDate.of(tuple.getYear(), tuple.getMonth(), tuple.getDay()));
    }

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "kind", ignore = true)
//    @Mapping(target = "etag", ignore = true)
//    @Mapping(target = "links", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "statementActionDate", ignore = true)
//    @Mapping(target = "statementType", ignore = true)
//    PscWithIdentificationFiling map(PscWithIdentificationDto dto);
//
//    PscWithIdentificationDto map(PscWithIdentificationFiling filing);
//
//    @Mapping(target = "countryRegistered", source = "identification.countryRegistered")
//    @Mapping(target = "placeRegistered", source = "identification.placeRegistered")
//    @Mapping(target = "registrationNumber", source = "identification.registrationNumber")
//    @Mapping(target = "legalAuthority", source = "identification.legalAuthority")
//    @Mapping(target = "legalForm", source = "identification.legalForm")
//    WithIdentificationFilingDataDto mapFiling(final PscWithIdentificationFiling entity);

}