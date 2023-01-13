package uk.gov.companieshouse.pscfiling.api.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.validation.FieldError;

/**
 * A validation Exception with {@link FieldError}s produced by Spring MVC.
 */
public class InvalidFilingException extends RuntimeException {
    private final List<FieldError> fieldErrors;

    public InvalidFilingException(final List<FieldError> fieldErrors) {
        this.fieldErrors = new ArrayList<>(fieldErrors);
    }

    public List<FieldError> getFieldErrors() {
        return Collections.unmodifiableList(fieldErrors);
    }
}
