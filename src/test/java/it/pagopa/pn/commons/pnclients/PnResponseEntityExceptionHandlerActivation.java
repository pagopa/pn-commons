package it.pagopa.pn.commons.pnclients;


import it.pagopa.pn.commons.exceptions.ExceptionHelper;
import it.pagopa.pn.commons.exceptions.PnResponseEntityExceptionHandler;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@org.springframework.web.bind.annotation.ControllerAdvice
@Import(ExceptionHelper.class)
@Order(-2)
public class PnResponseEntityExceptionHandlerActivation extends PnResponseEntityExceptionHandler {
    public PnResponseEntityExceptionHandlerActivation(ExceptionHelper exceptionHelper) {
        super(exceptionHelper);
    }
}
