package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Eccezione di validazione di base, viene tradotta con un errore 400
 * Pensata per tradurre facilmente la validation exception e per generare
 * i problem relativi ai problemi di validazione.
 */
public class PnValidationException extends PnRuntimeException {



    @Deprecated
    public <T> PnValidationException(String validationTargetId, Set<ConstraintViolation<T>> validationErrors) {
        this("Some parameters are invalid", new ExceptionHelper(Optional.empty()).generateProblemErrorsFromConstraintViolation(validationErrors), null  );
    }

    protected PnValidationException(String message, List<ProblemError> problemErrorList) {
        this( message,  problemErrorList, null  );
    }

    protected PnValidationException(String message, List<ProblemError> problemErrorList, Throwable cause) {
        super( HttpStatus.BAD_REQUEST.getReasonPhrase(), message, HttpStatus.BAD_REQUEST.value(), problemErrorList, cause  );
    }

    public static class PnValidationExceptionBuilder<T>
    {
        @Autowired
        private ExceptionHelper exceptionHelper;

        private Set<ConstraintViolation<T>> validationErrors;
        private List<ProblemError> problemErrorList;
        private Throwable cause;
        private String message;

        public PnValidationExceptionBuilder() {
        }

        public PnValidationExceptionBuilder validationErrors(Set<ConstraintViolation<T>> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }
        public PnValidationExceptionBuilder problemErrorList(List<ProblemError> problemErrorList) {
            this.problemErrorList = problemErrorList;
            return this;
        }
        public PnValidationExceptionBuilder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }
        public PnValidationExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        //Return the finally consrcuted PnValidationException object
        public PnValidationException build() {
            if (CollectionUtils.isEmpty(problemErrorList))
                return new PnValidationException(message, exceptionHelper.generateProblemErrorsFromConstraintViolation(this.validationErrors), cause );
            else
                return new PnValidationException(message, problemErrorList, cause );
        }
    }
}
