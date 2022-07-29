package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.*;

@Slf4j
@Component
public class ExceptionHelper {

    private Map<String, String> validationMap = new HashMap<>();

    public ExceptionHelper(Optional<IValidationCustomMapper> customValidationMapper){

        initValidationMap();

        if (customValidationMapper.isPresent())
            validationMap.putAll(customValidationMapper.get().getValidationCodeCustomMapping());

    }



    public Problem handleException(Throwable ex){
        // gestione exception e generazione fault
        Problem res;


        // se l'eccezione non è di tipo pnXXX, ne genero una generica per wrapparla, di fatto la tratto come 500
        if (!(ex instanceof IPnException))
        {
            ex = new PnInternalException("Errore generico", ERROR_CODE_PN_GENERIC_ERROR, ex);
        }

        res = ((IPnException) ex).getProblem();
        if (res.getStatus() >= 500)
            log.error("pn-exception " + res.getStatus() + " catched problem={}", res, ex);
        else
            log.warn("pn-exception " + res.getStatus() + " catched problem={}", res, ex);

        return res;
    }

    public String generateFallbackProblem(){
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
        fallback = fallback.replace("{errorcode}", ERROR_CODE_PN_GENERIC_ERROR);

        return fallback;
    }

    public <T> List<ProblemError> generateProblemErrorsFromConstraintViolation(Set<ConstraintViolation<T>> constraintViolations)
    {
        return constraintViolations.stream().map(constraintViolation -> ProblemError.builder()
                .code(getCodeFromAnnotation(constraintViolation.getConstraintDescriptor().getAnnotation()))
                .detail(constraintViolation.getMessage())
                .element(constraintViolation.getPropertyPath().toString())
                .build()).collect(Collectors.toList());
    }

    private String getCodeFromAnnotation(Annotation annotation){
        String annotationname = annotation.annotationType().getName();
        return validationMap.getOrDefault(annotationname, ERROR_CODE_PN_GENERIC_INVALIDPARAMETER);
    }


    private void initValidationMap() {
        this.validationMap.put(javax.validation.constraints.AssertFalse.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_ASSERTFALSE);
        this.validationMap.put(javax.validation.constraints.AssertFalse.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_ASSERTFALSE);
        this.validationMap.put(javax.validation.constraints.AssertTrue.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_ASSERTTRUE);
        this.validationMap.put(javax.validation.constraints.AssertTrue.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_ASSERTTRUE);

        this.validationMap.put(javax.validation.constraints.DecimalMax.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MAX);
        this.validationMap.put(javax.validation.constraints.DecimalMax.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MAX);
        this.validationMap.put(javax.validation.constraints.Max.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MAX);
        this.validationMap.put(javax.validation.constraints.Max.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MAX);
        this.validationMap.put(javax.validation.constraints.DecimalMin.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MIN);
        this.validationMap.put(javax.validation.constraints.DecimalMin.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MIN);
        this.validationMap.put(javax.validation.constraints.Min.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MIN);
        this.validationMap.put(javax.validation.constraints.Min.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_MIN);

        this.validationMap.put(javax.validation.constraints.Digits.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_DIGITS);
        this.validationMap.put(javax.validation.constraints.Digits.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_DIGITS);

        this.validationMap.put(javax.validation.constraints.Future.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_FUTURE);
        this.validationMap.put(javax.validation.constraints.Future.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_FUTURE);
        this.validationMap.put(javax.validation.constraints.FutureOrPresent.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_FUTUREORPRESENT);
        this.validationMap.put(javax.validation.constraints.FutureOrPresent.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_FUTUREORPRESENT);
        this.validationMap.put(javax.validation.constraints.Past.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_PAST);
        this.validationMap.put(javax.validation.constraints.Past.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_PAST);
        this.validationMap.put(javax.validation.constraints.PastOrPresent.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_PASTORPRESENT);
        this.validationMap.put(javax.validation.constraints.PastOrPresent.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_PASTORPRESENT);


        this.validationMap.put(javax.validation.constraints.NotNull.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED);
        this.validationMap.put(javax.validation.constraints.NotNull.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED);
        this.validationMap.put(javax.validation.constraints.NotBlank.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED);
        this.validationMap.put(javax.validation.constraints.NotBlank.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED);
        this.validationMap.put(javax.validation.constraints.NotEmpty.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED);
        this.validationMap.put(javax.validation.constraints.NotEmpty.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_REQUIRED);

        this.validationMap.put(javax.validation.constraints.Null.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_NULL);
        this.validationMap.put(javax.validation.constraints.Null.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_NULL);

        this.validationMap.put(javax.validation.constraints.Pattern.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_PATTERN);
        this.validationMap.put(javax.validation.constraints.Pattern.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_PATTERN);

        this.validationMap.put(javax.validation.constraints.Size.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_SIZE);
        this.validationMap.put(javax.validation.constraints.Size.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_SIZE);

        this.validationMap.put(javax.validation.constraints.Email.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_EMAIL);
        this.validationMap.put(javax.validation.constraints.Email.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_EMAIL);

        this.validationMap.put(javax.validation.constraints.Negative.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_NEGATIVE);
        this.validationMap.put(javax.validation.constraints.Negative.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_NEGATIVE);
        this.validationMap.put(javax.validation.constraints.NegativeOrZero.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_NEGATIVEORZERO);
        this.validationMap.put(javax.validation.constraints.NegativeOrZero.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_NEGATIVEORZERO);
        this.validationMap.put(javax.validation.constraints.Positive.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_POSITIVE);
        this.validationMap.put(javax.validation.constraints.Positive.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_POSITIVE);
        this.validationMap.put(javax.validation.constraints.PositiveOrZero.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_POSITIVEORZERO);
        this.validationMap.put(javax.validation.constraints.PositiveOrZero.List.class.getName(), ERROR_CODE_PN_GENERIC_INVALIDPARAMETER_POSITIVEORZERO);


    }
}
