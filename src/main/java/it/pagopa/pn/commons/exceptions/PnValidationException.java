package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;

/**
 * Eccezione di validazione di base, viene tradotta con un errore 400
 * Pensata per tradurre facilmente la validation exception e per generare
 * i problem relativi ai problemi di validazione.
 */
public class PnValidationException extends PnRuntimeException {

    public PnValidationException(String validationTargetId, Set<ConstraintViolation> validationErrors) {
        this("Some parameters are invalid", ExceptionHelper.generateProblemErrorsFromConstraintViolation(validationErrors), null  );
    }

    public PnValidationException(String message, List<ProblemError> problemErrorList) {
        this( message,  problemErrorList, null  );
    }

    public PnValidationException(String message, List<ProblemError> problemErrorList, Throwable cause) {
        super( HttpStatus.BAD_REQUEST.getReasonPhrase(), message, HttpStatus.BAD_REQUEST.value(), problemErrorList, cause  );
    }
}
