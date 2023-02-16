package uk.gov.companieshouse.pscfiling.api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.dto.FilingDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.IndividualFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Mapper(componentModel = "spring")//, uses = NameElementsMapper.class)
public interface FilingDataMapper {

    default PscCommunal enhance(@MappingTarget final PscCommunal filing, final PscApi details) {
        if (details == null) {
            return filing;
        }
        if (filing instanceof PscIndividualFiling) {
            return enhance((PscIndividualFiling) filing, details);
        }
        else if (filing instanceof PscWithIdentificationFiling) {
            return enhance((PscWithIdentificationFiling) filing, details);
        }
        else {
            return null;
        }
    }

    default PscIndividualFiling enhance(final PscIndividualFiling filing, final PscApi details) {
        return PscIndividualFiling.builder(filing).nameElements(map(details.getNameElements()))
                .build();
    }

    default PscWithIdentificationFiling enhance(final PscWithIdentificationFiling filing,
            final PscApi details) {
        return PscWithIdentificationFiling.builder(filing).name(details.getName())
                .build();
    }

    @Mapping(target = "otherForenames", source = "middleName")
    NameElements map(final NameElementsApi nameElementsApi);

    default FilingDtoCommunal map(final PscCommunal filing) {
        if (filing instanceof PscIndividualFiling) {
            return map((PscIndividualFiling) filing);
        }
        else if (filing instanceof PscWithIdentificationFiling) {
            return map((PscWithIdentificationFiling) filing);
        }
        else {
            return null;
        }
    }

    @Mapping(target = "firstName", source = "nameElements.forename")
    @Mapping(target = "lastName", source = "nameElements.surname")
    @Mapping(target = ".", source = "nameElements")
    IndividualFilingDataDto map(final PscIndividualFiling entity);

    @Mapping(target = ".", source = "identification")
    WithIdentificationFilingDataDto map(final PscWithIdentificationFiling entity);

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
    
}