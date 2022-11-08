package it.pagopa.pn.commons.exceptions.mapper;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import org.springframework.validation.FieldError;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_INVALIDPARAMETER;

public class FieldErrorToProblemErrorMapper {

    private FieldErrorToProblemErrorMapper(){}

    public static ProblemError toProblemError(FieldError fieldError)
    {
        return  ProblemError.builder()
                .code(ERROR_CODE_PN_GENERIC_INVALIDPARAMETER)
                .detail(fieldError.getDefaultMessage())
                .element(fieldError.getField())
                .build();
    }

}
