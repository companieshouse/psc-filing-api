package uk.gov.companieshouse.pscfiling.api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.pscfiling.api.model.dto.FilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;

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

    @Mapping(target = "title", source = "nameElements.title")
    @Mapping(target = "firstName", source = "nameElements.forename")
    @Mapping(target = "otherForenames", source = "nameElements.otherForenames")
    @Mapping(target = "lastName", source = "nameElements.surname")
    FilingDataDto mapFiling(final PscIndividualFiling entity);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    default String isoDateOfBirth(Date3Tuple tuple) {
        if (tuple == null) {
            return null;
        }
        return DateTimeFormatter.ISO_LOCAL_DATE.format(
            LocalDate.of(tuple.getYear(), tuple.getMonth(), tuple.getDay()));
    }

}
