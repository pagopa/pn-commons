package it.pagopa.pn.commons.exceptions;


import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.pn.common.rest.error.v1.dto.ProblemError;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static it.pagopa.pn.commons.exceptions.ExceptionHelper.ERROR_CODE_GENERIC_ERROR;

/**
 * Eccezione base da estendere all'occorrenza, genera già in automatico il problem da ritornare
 */
@Slf4j
public class PnRuntimeException extends RuntimeException implements IPnException {

    private static final String MDC_TRACE_ID_KEY = "trace_id";

    private final Problem problem;

    public PnRuntimeException(@NotNull String message,@NotNull  String description, int status,@NotNull  String errorcode, String element, String detail) {
        this(message, description, status, errorcode, element, detail, null);
    }

    public PnRuntimeException(@NotNull String message,@NotNull  String description, int status,@NotNull  String errorcode, String element, String detail, Throwable cause) {
        this(message, description, status, List.of(ProblemError.builder()
                .code(errorcode)
                .detail(detail)
                .element(element)
                .build()), cause);
    }

    public PnRuntimeException(@NotNull String message,@NotNull  String description, int status,@NotNull  List<ProblemError> problemErrorList) {
        this(message, description, status, problemErrorList, null);
    }

    public PnRuntimeException(@NotNull String message,@NotNull String description, int status,@NotNull List<ProblemError> problemErrorList, Throwable cause) {
        super(message, cause);
        problem = new Problem();
        problem.setTitle(message.substring(0, Math.min(message.length(), 64)));
        problem.setDetail(description.substring(0, Math.min(description.length(), 4096)));
        problem.setStatus(status<100?100:(Math.min(status, 600)));
        problem.setTimestamp(Instant.now().atOffset(ZoneOffset.UTC));

        // non deve mai essere vuoto, quindi se per qualche motivo lo è, aggiungo un errore generico
        if (problemErrorList.isEmpty())
        {
            problemErrorList.add(ProblemError.builder()
                            .code(ERROR_CODE_GENERIC_ERROR)
                    .build());
        }
        problem.setErrors(problemErrorList);

        try {
            problem.setTraceId(MDC.get(MDC_TRACE_ID_KEY));
        } catch (Exception e) {
            log.warn("Cannot get traceid", e);
        }
    }

    public int getStatus(){ return problem.getStatus(); }

    public Problem getProblem() {
        return problem;
    }
}
