package uk.gov.companieshouse.pscfiling.api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscIndividualDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.PscWithIdentificationDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Mapper(componentModel = "spring")
public interface PscMapper {

    default PscCommunal map(final PscDtoCommunal dto) {
        if (dto instanceof PscIndividualDto pscDto) {
            return map(pscDto);
        }
        else if (dto instanceof PscWithIdentificationDto pscDto) {
            return map(pscDto);
        }
        else {
            return null;
        }
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "links", ignore = true)
    @Mapping(target = "statementActionDate", ignore = true)
    @Mapping(target = "statementType", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PscIndividualFiling map(final PscIndividualDto dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "etag", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "kind", ignore = true)
    @Mapping(target = "links", ignore = true)
    @Mapping(target = "statementActionDate", ignore = true)
    @Mapping(target = "statementType", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PscWithIdentificationFiling map(final PscWithIdentificationDto dto);

    default PscDtoCommunal map(final PscCommunal filing) {
        if (filing instanceof PscIndividualFiling pscFiling) {
            return map(pscFiling);
        }
        else if (filing instanceof PscWithIdentificationFiling pscFiling) {
            return map(pscFiling);
        }
        else {
            return null;
        }
    }

    PscIndividualDto map(final PscIndividualFiling filing);

    PscWithIdentificationDto map(final PscWithIdentificationFiling filing);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    default String isoDateOfBirth(final Date3Tuple tuple) {
        if (tuple == null) {
            return null;
        }

        return DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.of(tuple.year(), tuple.month(), tuple.day()));
    }

    @Mapping(target = "day", ignore = true)
    Date3Tuple map(final DateOfBirth dob);

}