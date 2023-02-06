package uk.gov.companieshouse.pscfiling.api.validator;

public interface WithIdentificationFilingValid {
    /**
     * @param validationContext the data to be validated plus necessary details
     */
    void validate(final WithIdentificationFilingValidationContext validationContext);

    void setNext(WithIdentificationFilingValid individualFilingValidator);
}
