package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import it.pagopa.pn.commons.exceptions.mapper.ConstraintViolationToProblemErrorMapper;
import it.pagopa.pn.commons.exceptions.mapper.FieldErrorToProblemErrorMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.*;
import static it.pagopa.pn.commons.log.MDCWebFilter.MDC_TRACE_ID_KEY;

@Slf4j
@Component
public class ExceptionHelper {

    public static final String MESSAGE_SEE_LOGS_FOR_DETAILS = "See logs for details in ";
    public static final String MESSAGE_UNEXPECTED_ERROR = "Unexpected error";
    public static final String MESSAGE_HANDLED_ERROR = "Handled error";
    private final Map<String, String> validationMap = new HashMap<>();

    @Value("${spring.application.name:}")
    private String applicationName;

    public ExceptionHelper(Optional<IValidationCustomMapper> customValidationMapper){

        initValidationMap();

        customValidationMapper.ifPresent(iValidationCustomMapper -> validationMap.putAll(iValidationCustomMapper.getValidationCodeCustomMapping()));

    }



    public Problem handleException(Throwable ex){
        // gestione exception e generazione fault
        Problem res;

        // gestione dedicata delle constraintviolation, lanciate da spring direttamente
        if (ex instanceof javax.validation.ConstraintViolationException) {
            javax.validation.ConstraintViolationException cex = (javax.validation.ConstraintViolationException)ex;
            // eccezione di constraint, recupero le info dei campi
            ex = new PnValidationExceptionBuilder<>(this)
                    .validationErrors(cex.getConstraintViolations())
                    .cause(ex)
                    .message(ex.getMessage())
                    .build();
        }
        else if (ex instanceof org.springframework.web.bind.support.WebExchangeBindException){
            org.springframework.web.bind.support.WebExchangeBindException cex = (org.springframework.web.bind.support.WebExchangeBindException)ex;
            // eccezione di spring riguardante errori di validazione, recupero le info dei campi
            ex = new PnValidationExceptionBuilder<>(this)
                    .fieldErrors(cex.getFieldErrors())
                    .cause(ex)
                    .message(ex.getMessage())
                    .build();
        }
        else if (ex instanceof org.springframework.web.server.ResponseStatusException){
            org.springframework.web.server.ResponseStatusException cex = (org.springframework.web.server.ResponseStatusException)ex;
            // eccezione di spring riguardante errori di altra natura
            ex = new PnRuntimeException(Objects.requireNonNull(cex.getMessage()==null?"Web error":cex.getMessage()),
                                        Objects.requireNonNull(cex.getReason()==null?"Web error":cex.getReason()),
                                        cex.getRawStatusCode(),
                                        ERROR_CODE_PN_WEB_GENERIC_ERROR, null, null, cex);
        }

        // se l'eccezione non è di tipo pnXXX, ne genero una generica per wrapparla, di fatto la tratto come 500
        if (!(ex instanceof IPnException))
        {
            ex = new PnInternalException("Errore generico", ERROR_CODE_PN_GENERIC_ERROR, ex);
        }

        res = ((IPnException) ex).getProblem();

        // nel caso in cui il traceid non fosse disponibile, magari lo è in questo momento, provo ad aggiungerlo
        enrichWithTraceIdIfMissing(res);

        if (res.getStatus() >= 500)
            log.error("pn-exception " + res.getStatus() + " catched problem={}", res, ex);
        else
            log.warn("pn-exception " + res.getStatus() + " catched problem={}", res, ex);

        return offuscateProblem(res);
    }

    private void enrichWithTraceIdIfMissing(Problem res){
        if (res.getTraceId() == null)
        {
            try {
                res.setTraceId(MDC.get(MDC_TRACE_ID_KEY));
            } catch (Exception var7) {
                log.warn("Cannot get traceid", var7);
            }
        }
        // se è ancora nullo, ci metto un uuid
        if (res.getTraceId() == null)
        {
            res.setTraceId("FALLBACK-UUID:" + UUID.randomUUID().toString());
        }
    }

    private Problem offuscateProblem(Problem res){
        if (res.getStatus() >= 500)
        {
            res.setTitle(MESSAGE_UNEXPECTED_ERROR);
        }
        else
        {
            res.setTitle(MESSAGE_HANDLED_ERROR);
        }

        res.setDetail(MESSAGE_SEE_LOGS_FOR_DETAILS + getCurrentApplicationName());

        return res;
    }

    private String getCurrentApplicationName(){
        return applicationName;
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

    public List<ProblemError> generateProblemErrorsFromConstraintViolation(Set<? extends ConstraintViolation<?>> constraintViolations)
    {
        return constraintViolations.stream().map(constraintViolation ->
               ConstraintViolationToProblemErrorMapper.toProblemError(constraintViolation, this)).collect(Collectors.toList());
    }


    public List<ProblemError> generateProblemErrorsFromFieldError(List<FieldError> fieldErrors)
    {
        return fieldErrors.stream().map(FieldErrorToProblemErrorMapper::toProblemError).collect(Collectors.toList());
    }

    public String getCodeFromAnnotation(Annotation annotation){
        String annotationname = annotation!=null?annotation.annotationType().getName():null;
        if (validationMap.containsKey(annotationname))
        {
            return validationMap.get(annotationname);
        }
        else if (annotationname == null)
        {
            log.warn("Annotation is null");
            return ERROR_CODE_PN_GENERIC_INVALIDPARAMETER;
        }
        else
        {
            log.error("Annotation {} not found", annotationname);
            return ERROR_CODE_PN_GENERIC_INVALIDPARAMETER;
        }

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
