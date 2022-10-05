package it.pagopa.pn.commons.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Handler pensato per essere attivato dai microservizi CLASSICI tramite:
 *
 * @org.springframework.web.bind.annotation.ControllerAdvice
 * @Import(ExceptionHelper.class)
 */
@Slf4j
public class PnResponseEntityExceptionHandler {

    private final ExceptionHelper exceptionHelper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PnResponseEntityExceptionHandler(ExceptionHelper exceptionHelper) {
        this.exceptionHelper = exceptionHelper;
        if (true) {
            objectMapper.findAndRegisterModules();
            objectMapper
                    .configOverride(OffsetDateTime.class)
                    .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
        }
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<Problem> handleRuntimeException(RuntimeException ex ) {

        try {
            Problem problem = exceptionHelper.handleException(ex);

            return ResponseEntity.status(problem.getStatus()).body(problem);
        } catch (Exception e) {
            log.error("cannot output problem", e);
            try (JsonParser reader = objectMapper.createParser(exceptionHelper.generateFallbackProblem())){
                Problem fallbackproblem = reader.readValueAs(Problem.class);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fallbackproblem);
            } catch (Exception exc) {
                log.error("cannot output fallback problem", exc);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
