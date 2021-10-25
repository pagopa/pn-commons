package it.pagopa.pn.commons.exceptions;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.Set;

public class PnValidationException extends IllegalArgumentException {

    private final transient String validationTargetId;
    private final transient Set<ConstraintViolation> validationErrors;

    public <T> PnValidationException(String validationTargetId, Set<ConstraintViolation<T>> validationErrors) {
        super( validationErrors.toString() );
        this.validationTargetId = validationTargetId;
        this.validationErrors = Collections.unmodifiableSet( validationErrors );
    }

    public Set<ConstraintViolation> getValidationErrors() {
        return validationErrors;
    }

    public String getValidationTargetId() {
        return validationTargetId;
    }
}
