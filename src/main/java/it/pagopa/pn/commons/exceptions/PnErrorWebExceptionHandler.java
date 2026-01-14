package it.pagopa.pn.commons.exceptions;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.time.OffsetDateTime;

/**
 * Handler pensato per essere attivato dai microservizi REACTIVE tramite:
 *
 * @Component
 * @Order(-2)
 * @Import(ExceptionHelper.class
 *
 */
@Slf4j
public class PnErrorWebExceptionHandler implements ErrorWebExceptionHandler {

  private final ExceptionHelper exceptionHelper;
  private final ObjectMapper objectMapper;

  public PnErrorWebExceptionHandler(ExceptionHelper exceptionHelper) {
    this.objectMapper = new ObjectMapper();
    this.exceptionHelper = exceptionHelper;
    objectMapper.findAndRegisterModules();
    objectMapper
            .configOverride(OffsetDateTime.class)
            .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
  }

  public PnErrorWebExceptionHandler(ExceptionHelper exceptionHelper, ObjectMapper objectMapper) {
    this.exceptionHelper = exceptionHelper;
    this.objectMapper = objectMapper;
    objectMapper.findAndRegisterModules();
    objectMapper
            .configOverride(OffsetDateTime.class)
            .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
  }

  @Override
  @NonNull
  public Mono<Void> handle(@NonNull ServerWebExchange serverWebExchange, @NonNull Throwable throwable) {
    DataBuffer dataBuffer;
    DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();

    try {
      Problem problem = exceptionHelper.handleException(throwable);

      serverWebExchange.getResponse().setStatusCode(HttpStatus.resolve(problem.getStatus()));

      dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(problem));
    } catch (JsonProcessingException e) {
      log.error("cannot output problem", e);
      dataBuffer = bufferFactory.wrap(exceptionHelper.generateFallbackProblem().getBytes());
    }
    serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
    

  } 

}