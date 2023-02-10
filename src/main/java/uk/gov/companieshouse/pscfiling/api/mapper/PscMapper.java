package uk.gov.companieshouse.pscfiling.api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.dto.IndividualFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Mapper(componentModel = "spring")//, uses = NameElementsMapper.class)
public interface PscMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "links", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @SubclassMapping(source = PscIndividualDto.class, target = PscIndividualFiling.class)
    @SubclassMapping(source = PscWithIdentificationDto.class,
            target = PscWithIdentificationFiling.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    PscCommunal map(PscDtoCommunal dto);

    @SubclassMapping(source = PscIndividualFiling.class, target = PscIndividualDto.class)
    @SubclassMapping(source = PscWithIdentificationFiling.class,
            target = PscWithIdentificationDto.class)
    @BeanMapping(subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
    PscDtoCommunal map(PscCommunal filing);

    @Mapping(target = "otherForenames", source = "middleName")
    NameElements map(final NameElementsApi nameElementsApi);

    @Mapping(target = "nameElements.otherForenames", source = "nameElements.middleName")
    @Mapping(target = ".", source = "nameElements")
    default PscIndividualFiling enhance(@MappingTarget final PscIndividualFiling pscFiling,
            final PscApi pscDetails) {
        return PscIndividualFiling.builder(pscFiling)
                .nameElements(map(pscDetails.getNameElements()))
                .build();
    }

    @Mapping(target = "title", source = "nameElements.title")
    @Mapping(target = "firstName", source = "nameElements.forename")
    @Mapping(target = "otherForenames", source = "nameElements.otherForenames")
    @Mapping(target = "lastName", source = "nameElements.surname")
    IndividualFilingDataDto mapFilingData(final PscIndividualFiling entity);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    default String isoDateOfBirth(final Date3Tuple tuple) {
        if (tuple == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE.format(
                LocalDate.of(tuple.getYear(), tuple.getMonth(), tuple.getDay()));
    }

    @Mapping(target = "day", ignore = true)
    Date3Tuple map(final DateOfBirth dob);

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
//    WithIdentificationFilingDataDto mapFilingData(final PscWithIdentificationFiling entity);

}