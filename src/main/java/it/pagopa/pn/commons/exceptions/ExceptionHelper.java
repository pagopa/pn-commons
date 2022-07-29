package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class ExceptionHelper {

    public static final String ERROR_CODE_GENERIC_ERROR = "PN_GENERIC_ERROR";
    public static final String ERROR_CODE_INVALID_PARAMETER = "PN_INVALID_PARAMETER";

    private ExceptionHelper(){}

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
            log.error("pn-exception " + res.getStatus() + " catched problem={}", res, ex);
        else
            log.warn("pn-exception " + res.getStatus() + " catched problem={}", res, ex);

        return res;
    }

    public static String generateFallbackProblem(){
        String fallback = "{\n" +
                "    \"status\": 500,\n" +
                "    \"title\": \"Internal Server Error\",\n" +
                "    \"detail\": \"Cannot output problem\",\n" +
                "    \"traceId\": \"{traceid}\",\n" +
                "    \"timestamp\": \"{timestamp}\",\n" +
                "    \"errors\": [\n" +
                "        {\n" +
                "            \"code\": \"{errorcode}\",\n" +
                "            \"element\": null,\n" +
                "            \"detail\": null\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        fallback = fallback.replace("{traceid}", UUID.randomUUID().toString());
        fallback = fallback.replace("{timestamp}", Instant.now().toString());
        fallback = fallback.replace("{errorcode}", ERROR_CODE_GENERIC_ERROR);

        return fallback;
    }

    public static List<ProblemError> generateProblemErrorsFromConstraintViolation(Set<ConstraintViolation> constraintViolations)
    {
        return constraintViolations.stream().map(constraintViolation -> ProblemError.builder()
                .code(ERROR_CODE_INVALID_PARAMETER)
                .detail(constraintViolation.getMessage())
                .element(getNameFromPath(constraintViolation.getPropertyPath()))
                .build()).collect(Collectors.toList());
    }

    private static String getNameFromPath(Path path){
        String fieldName = null;
        for (Path.Node node : path) {
            fieldName = node.getName();
        }
        return fieldName;
    }
}
