package it.pagopa.pn.commons.exceptions;


import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;
import static it.pagopa.pn.commons.log.MDCWebFilter.MDC_TRACE_ID_KEY;

/**
 * Eccezione base da estendere all'occorrenza, genera già in automatico il problem da ritornare 
 */
@Slf4j
public class PnRuntimeException extends NestedRuntimeException implements IPnException {

    private final Problem problem;

    public PnRuntimeException(@NotNull String message,@NotNull  String description, int status,@NotNull  String errorcode, String element, String detail) {
        this(message, description, status, errorcode, element, detail, null);
    }

    public PnRuntimeException(@NotNull String message,@NotNull  String description, int status,@NotNull  String errorcode, String element, String detail, Throwable cause) {
        this(message, description, status, List.of(it.pagopa.pn.commons.exceptions.dto.ProblemError.builder()
                .code(errorcode)
                .detail(detail)
                .element(element)
                .build()), cause);
    }

    public PnRuntimeException(@NotNull String message,@NotNull  String description, int status,@NotNull  List<it.pagopa.pn.commons.exceptions.dto.ProblemError> problemErrorList) {
        this(message, description, status, problemErrorList, null);
    }

    public PnRuntimeException(@NotNull String message,@NotNull String description, int status,@NotNull List<it.pagopa.pn.commons.exceptions.dto.ProblemError> problemErrorList, Throwable cause) {
        super(message, cause);
        problem = new Problem();

        if (!StringUtils.hasText(message))
            message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        if (!StringUtils.hasText(description))
            description = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        if (CollectionUtils.isEmpty(problemErrorList))
            problemErrorList = new ArrayList<>();

        problem.setType("GENERIC_ERROR");
        problem.setTitle(message.substring(0, Math.min(message.length(), 64)));
        problem.setDetail(description.substring(0, Math.min(description.length(), 4096)));
        problem.setStatus(status<100?100:(Math.min(status, 600)));
        problem.setTimestamp(Instant.now().atOffset(ZoneOffset.UTC));

        // non deve mai essere vuoto, quindi se per qualche motivo lo è, aggiungo un errore generico
        if (problemErrorList.isEmpty())
        {
            problemErrorList.add(it.pagopa.pn.commons.exceptions.dto.ProblemError.builder()
                            .code(ERROR_CODE_PN_GENERIC_ERROR)
                            .detail("none")
                    .build());
        }
        problem.setErrors(problemErrorList.stream().map(problemError -> {
                    if (problemError.getDetail()!=null)
                        problemError.setDetail(problemError.getDetail().substring(0, Math.min(problemError.getDetail().length(), 1024)));
                    else
                        problemError.setDetail("none");

                    // mappo nel probleerror generato dallo YAML
                    return it.pagopa.pn.common.rest.error.v1.dto.ProblemError.builder()
                            .code(problemError.getCode())
                            .detail(problemError.getDetail())
                            .element(problemError.getElement())
                            .build();
                }).toList());

        try {
            problem.setTraceId(MDC.get(MDC_TRACE_ID_KEY));
        } catch (Exception e) {
            log.warn("Cannot get traceid", e);
        }
    }

    public Problem getProblem() {
        return problem;
    }
}
