package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;

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


    /**
     * @deprecated
     * Costruttore deprecato, inserito per retro compatibilità
     * Usare il builder o estendere la classe utilizzando i costruttori che prevedono i ProblemError
     *
     * @param validationTargetId non usato
     * @param validationErrors errori di validazione
     * @param <T> tipo errori validazione
     */
    @Deprecated()
    public <T> PnValidationException(String validationTargetId, Set<? extends ConstraintViolation<?>> validationErrors) {
        this("Some parameters are invalid", new ExceptionHelper(Optional.empty()).generateProblemErrorsFromConstraintViolation(validationErrors), null  );
    }

    protected PnValidationException(String message, List<ProblemError> problemErrorList) {
        this( message,  problemErrorList, null  );
    }

    protected PnValidationException(String message, List<ProblemError> problemErrorList, Throwable cause) {
        super( HttpStatus.BAD_REQUEST.getReasonPhrase(), message, HttpStatus.BAD_REQUEST.value(), problemErrorList, cause  );
    }

}
