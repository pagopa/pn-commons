package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;

import java.util.Map;
import java.util.stream.Collectors;

public class PnIdConflictException extends PnValidationException {

    public PnIdConflictException(Map<String, String> invalidFields) {
        this(PnExceptionsCodes.ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_DUPLICATED, invalidFields);
    }


    public PnIdConflictException(String errorCode, Map<String, String> invalidFields) {
       this(errorCode, invalidFields, null);
    }

    public PnIdConflictException(String errorCode, Map<String, String> invalidFields, Throwable cause) {
        super("Input duplicato",
                invalidFields.keySet().stream().map(x -> ProblemError.builder()
                        .code(errorCode)
                        .element(x)
                        .detail(x  + "=" + invalidFields.get(x))
                        .build()).collect(Collectors.toList()), cause );
    }
}
