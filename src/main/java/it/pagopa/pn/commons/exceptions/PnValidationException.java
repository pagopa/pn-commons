package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.ProblemError;

import javax.validation.ConstraintViolation;
import java.util.Collections;
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
        super( "Invalid request", message, 400, problemErrorList, cause  );
    }
}
