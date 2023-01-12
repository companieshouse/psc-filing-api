package uk.gov.companieshouse.pscfiling.api.error;

import java.util.Collections;
import java.util.List;
import org.springframework.validation.FieldError;

/**
 * A validation Exception with {@link FieldError}s produced by Spring MVC.
 */
public class InvalidFilingException extends RuntimeException {
    private final List<FieldError> fieldErrors;
    private final ApiErrors apiErrors;


    public InvalidFilingException(final List<FieldError> fieldErrors, final ApiErrors apiErrors) {
        this.fieldErrors = fieldErrors;
        this.apiErrors = apiErrors;
    }


    public List<FieldError> getFieldErrors() {
        return Collections.unmodifiableList(fieldErrors);
    }

    public ApiErrors getApiErrors() {
        return apiErrors;
    }
}
