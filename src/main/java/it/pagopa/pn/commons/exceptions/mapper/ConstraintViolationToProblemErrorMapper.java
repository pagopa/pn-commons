package it.pagopa.pn.commons.exceptions.mapper;

import it.pagopa.pn.commons.exceptions.ExceptionHelper;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;

import jakarta.validation.ConstraintViolation;

public class ConstraintViolationToProblemErrorMapper {

    private ConstraintViolationToProblemErrorMapper(){}

    public static ProblemError toProblemError(ConstraintViolation<?> constraintViolation, ExceptionHelper exceptionHelper)
    {
        return ProblemError.builder()
                .code(exceptionHelper.getCodeFromAnnotation(constraintViolation.getConstraintDescriptor() == null?null:constraintViolation.getConstraintDescriptor().getAnnotation()))
                .detail(constraintViolation.getMessage())
                .element(constraintViolation.getPropertyPath()==null?null:constraintViolation.getPropertyPath().toString())
                .build();
    }

}
