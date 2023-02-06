package uk.gov.companieshouse.pscfiling.api.validator;

public interface IndividualFilingValid {
    /**
     * @param validationContext the data to be validated plus necessary details
     */
    void validate(final IndividualFilingValidationContext validationContext);

    void setNext(IndividualFilingValid individualFilingValidator);
}
