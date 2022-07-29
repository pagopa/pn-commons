package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handler pensato per essere attivato dai microservizi CLASSICI tramite:
 *
 * @org.springframework.web.bind.annotation.ControllerAdvice
 */
@Slf4j
public class PnResponseEntityExceptionHandler {

    private final ExceptionHelper exceptionHelper;

    public PnResponseEntityExceptionHandler(ExceptionHelper exceptionHelper) {
        this.exceptionHelper = exceptionHelper;
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Problem> handleRuntimeException(RuntimeException ex ) {

        Problem problem = exceptionHelper.handleException(ex);

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }
}
