package uk.gov.companieshouse.pscfiling.api.validator;

import uk.gov.companieshouse.pscfiling.api.model.dto.PscDtoCommunal;

public interface FilingValid {
    /**
     * @param validationContext the data to be validated plus necessary details
     */
    <T extends PscDtoCommunal> void validate(final FilingValidationContext<T> validationContext);

    void setNext(final FilingValid filingValidator);
}
