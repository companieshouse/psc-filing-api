package uk.gov.companieshouse.pscfiling.api.validator;

import uk.gov.companieshouse.pscfiling.api.model.dto.WithIdentificationFilingDataDto;

public interface WithIdentificationFilingValid {
    /**
     * @param validationContext the data to be validated plus necessary details
     */
    void validate(final FilingValidationContext <WithIdentificationFilingDataDto> validationContext);

    void setNext(WithIdentificationFilingValid individualFilingValidator);
}