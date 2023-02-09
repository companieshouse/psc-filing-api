package uk.gov.companieshouse.pscfiling.api.exception;

import java.util.List;
import org.springframework.validation.FieldError;

public class ConflictingFilingException extends InvalidFilingException {
    public ConflictingFilingException(List<FieldError> fieldErrors) {
        super(fieldErrors);
    }
}
