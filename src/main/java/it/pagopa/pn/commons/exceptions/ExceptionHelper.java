package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.common.rest.error.v1.dto.ProblemError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ExceptionHelper {

    public static final String ERROR_CODE_GENERIC_ERROR = "PN_GENERIC_ERROR";
    public static final String ERROR_CODE_INVALID_PARAMETER = "PN_INVALID_PARAMETER";

    private ExceptionHelper(){}

    public static HttpStatus getHttpStatusFromException(Throwable ex){
        if (ex instanceof PnRuntimeException)
        {
            return HttpStatus.resolve(((PnRuntimeException) ex).getStatus());
        }
        else
            return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public static Problem handleException(Throwable ex){
        // gestione exception e generazione fault
        Problem res;


        // se l'eccezione non è di tipo pnXXX, ne genero una generica per wrapparla, di fatto la tratto come 500
        if (!(ex instanceof IPnException))
        {
            ex = new PnInternalException("Errore generico", ex);
        }

        res = ((IPnException) ex).getProblem();
        if (res.getStatus() >= 500)
            log.error("pn-exception " + res.getStatus() + " catched", ex);
        else
            log.warn("pn-exception " + res.getStatus() + " catched", ex);

        return res;
    }

    public static List<ProblemError> generateProblemErrorsFromConstraintViolation(Set<ConstraintViolation> constraintViolations)
    {
        return constraintViolations.stream().map(constraintViolation -> ProblemError.builder()
                .code(ERROR_CODE_INVALID_PARAMETER)
                .detail(constraintViolation.getMessage())
                .element(constraintViolation.getPropertyPath().toString())
                .build()).collect(Collectors.toList());
    }
}
