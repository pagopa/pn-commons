package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

public class PnValidationExceptionBuilder<T> {
    private final ExceptionHelper exceptionHelper;

    private Set<ConstraintViolation<? extends Object>> validationErrors;
    private List<FieldError> fieldErrors;
    private List<ProblemError> problemErrorList;
    private Throwable cause;
    private String message;

    public PnValidationExceptionBuilder(ExceptionHelper exceptionHelper) {
        this.exceptionHelper = exceptionHelper;
    }

    public PnValidationExceptionBuilder<T> validationErrors(Set<ConstraintViolation<? extends Object>> validationErrors) {
        this.validationErrors = validationErrors;
        return this;
    }

    public PnValidationExceptionBuilder<T> fieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
        return this;
    }

    public PnValidationExceptionBuilder<T> problemErrorList(List<ProblemError> problemErrorList) {
        this.problemErrorList = problemErrorList;
        return this;
    }

    public PnValidationExceptionBuilder<T> cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public PnValidationExceptionBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    //Return the finally consrcuted PnValidationException object
    public PnValidationException build() {
        if (!CollectionUtils.isEmpty(validationErrors))
            return new PnValidationException(message, exceptionHelper.generateProblemErrorsFromConstraintViolation(this.validationErrors), cause);
        else if (!CollectionUtils.isEmpty(fieldErrors))
            return new PnValidationException(message, exceptionHelper.generateProblemErrorsFromFieldError(this.fieldErrors), cause);
        else
            return new PnValidationException(message, problemErrorList, cause);
    }
}
