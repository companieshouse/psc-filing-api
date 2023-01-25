package uk.gov.companieshouse.pscfiling.api.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.validation.FieldError;
import uk.gov.companieshouse.api.model.validationstatus.ValidationStatusError;
import uk.gov.companieshouse.pscfiling.api.error.ErrorType;
import uk.gov.companieshouse.pscfiling.api.error.LocationType;

@Mapper(componentModel = "spring", imports = {ErrorType.class, LocationType.class})
public interface ErrorMapper {
    @Mapping(target = "error", source = "defaultMessage")
    @Mapping(target="type", expression = "java(ErrorType.VALIDATION.getType())")
    @Mapping(target="location", expression = "java(\"$.\" + fieldError.getField())")
    @Mapping(target="locationType", expression = "java(LocationType.JSON_PATH.getValue())")
    ValidationStatusError map(final FieldError fieldError);

    ValidationStatusError[] map(final List<FieldError> fieldErrors);
}
