package uk.gov.companieshouse.pscfiling.api.validator;

public interface FilingValid {
    /**
     * @param validationContext the data to be validated plus necessary details
     */
    void validate(final FilingValidationContext validationContext);

    void setNext(FilingValid filingValidator);
}
