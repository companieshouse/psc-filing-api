package uk.gov.companieshouse.pscfiling.api.mapper;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.api.model.common.DateOfBirth;
import uk.gov.companieshouse.api.model.psc.NameElementsApi;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.dto.FilingDtoCommunal;
import uk.gov.companieshouse.pscfiling.api.model.dto.IndividualFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;
import uk.gov.companieshouse.pscfiling.api.model.entity.Date3Tuple;
import uk.gov.companieshouse.pscfiling.api.model.entity.Identification;
import uk.gov.companieshouse.pscfiling.api.model.entity.NameElements;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscCommunal;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscWithIdentificationFiling;

@Mapper(componentModel = "spring")//, uses = NameElementsMapper.class)
public interface FilingDataMapper {

    default PscCommunal enhance(@MappingTarget final PscCommunal filing,
            final PscTypeConstants pscType, final PscApi details) {
        if (details == null || filing == null) {
            return filing;
        }

        switch (pscType) {
            case INDIVIDUAL:
                return enhanceIndividual((PscIndividualFiling) filing, details);

            case CORPORATE_ENTITY:
                return enhanceCorporateEntity((PscWithIdentificationFiling) filing, details);

            case LEGAL_PERSON:
                return enhanceLegalPerson((PscWithIdentificationFiling) filing, details);

            default:
                throw new UnsupportedOperationException(
                        MessageFormat.format("PSC type {0} not supported", pscType.name()));
        }
    }

    default PscIndividualFiling enhanceIndividual(@MappingTarget final PscIndividualFiling filing,
            final PscApi details) {

        return PscIndividualFiling.builder(filing).nameElements(map(details.getNameElements()))
                .build();
    }

    default PscWithIdentificationFiling enhanceCorporateEntity(
            final PscWithIdentificationFiling filing, final PscApi details) {

        return PscWithIdentificationFiling.builder(filing).name(details.getName())
                .build();
    }

    default PscWithIdentificationFiling enhanceLegalPerson(final PscWithIdentificationFiling filing,
            final PscApi details) {

        return PscWithIdentificationFiling.builder(filing).name(details.getName())
                .build();
    }

    @Mapping(target = "otherForenames", source = "middleName")
    NameElements map(final NameElementsApi nameElementsApi);

    Identification map(final uk.gov.companieshouse.api.model.psc.Identification identificationApi);

    default FilingDtoCommunal map(final PscCommunal filing, final PscTypeConstants pscType) {

        switch (pscType) {
            case INDIVIDUAL:
                return mapIndividual((PscIndividualFiling) filing);

            case CORPORATE_ENTITY:
                return mapCorporateEntity((PscWithIdentificationFiling) filing);

            case LEGAL_PERSON:
                return mapLegalPerson((PscWithIdentificationFiling) filing);

            default:
                return null;
        }
    }

    @Mapping(target = "firstName", source = "nameElements.forename")
    @Mapping(target = "lastName", source = "nameElements.surname")
    @Mapping(target = ".", source = "nameElements")
    IndividualFilingDataDto mapIndividual(final PscIndividualFiling entity);

    @Mapping(target = ".", source = "identification")
    WithIdentificationFilingDataDto mapCorporateEntity(final PscWithIdentificationFiling entity);
    @Mapping(target = "countryRegistered", ignore = true)
    @Mapping(target = "placeRegistered", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = ".", source = "identification")
    WithIdentificationFilingDataDto mapLegalPerson(final PscWithIdentificationFiling entity);

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