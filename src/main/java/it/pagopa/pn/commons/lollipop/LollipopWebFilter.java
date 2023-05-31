package it.pagopa.pn.commons.lollipop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.common.rest.error.v1.dto.Problem;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommand;
import it.pagopa.tech.lollipop.consumer.command.LollipopConsumerCommandBuilder;
import it.pagopa.tech.lollipop.consumer.model.CommandResult;
import it.pagopa.tech.lollipop.consumer.model.LollipopConsumerRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_LOLLIPOP_AUTH;
import static it.pagopa.pn.commons.utils.MDCUtils.MDC_TRACE_ID_KEY;
import static it.pagopa.tech.lollipop.consumer.command.impl.LollipopConsumerCommandImpl.VERIFICATION_SUCCESS_CODE;

@Slf4j
public class LollipopWebFilter implements WebFilter {
    private final LollipopConsumerCommandBuilder consumerCommandBuilder;

    @Autowired
    private ObjectMapper objectMapper;
    private static final String HEADER_FIELD = "x-pagopa-pn-src-ch";
    private static final String HEADER_VALUE = "IO";

    public LollipopWebFilter(LollipopConsumerCommandBuilder consumerCommandBuilder) {
        this.consumerCommandBuilder = consumerCommandBuilder;
    }
    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = exchange.getRequest().getHeaders();

        if (headers.containsKey(HEADER_FIELD)
                && Objects.equals(headers.getFirst(HEADER_FIELD), HEADER_VALUE)) {
            HttpMethod method = request.getMethod();

            // Get request body as String
            if (method != HttpMethod.GET && method != HttpMethod.DELETE) {
                return request.getBody()
                        .map(buffer -> buffer.toString(StandardCharsets.UTF_8))
                        .defaultIfEmpty("")
                        .flatMap(reqBody -> validateRequest(exchange, request, reqBody))
                        .collectList()
                        .flatMap(requests -> chain.filter(exchange));
            } else {
                return validateRequest(exchange, request, null)
                .flatMap(stringMono -> chain.filter(exchange));
            }
        }
        return chain.filter(exchange);
    }

    private Mono validateRequest(@NotNull ServerWebExchange exchange, ServerHttpRequest request, String requestBody) {
        // Get request parameters as Map<String, String[]>
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        Map<String, String[]> requestParams = new HashMap<>();
        queryParams.forEach((key, values) -> requestParams.put(key, values.toArray(new String[0])));

        // Get header parameters as Map<String, String>
        Map<String, String> headerParams = request.getHeaders().toSingleValueMap();

        // Create LollipopConsumerRequest object
        LollipopConsumerRequest consumerRequest = LollipopConsumerRequest.builder()
                .requestBody( requestBody )
                .requestParams( requestParams )
                .headerParams( headerParams )
                .build();

        LollipopConsumerCommand command = consumerCommandBuilder.createCommand(consumerRequest);
        CommandResult commandResult = command.doExecute();

        if (!commandResult.getResultCode().equals(VERIFICATION_SUCCESS_CODE)) {
            exchange.getResponse().setStatusCode( HttpStatus.NOT_FOUND );
            // Non voglio restituire l'errore al client ma lo loggo a livello warning
            log.warn("Lollipop auth response={}, detail={}", commandResult.getResultCode(), commandResult.getResultMessage());
            Problem problem = new Problem()
                    .timestamp(Instant.now().atOffset(ZoneOffset.UTC))
                    .detail(commandResult.getResultCode())
                    .traceId(MDC.get(MDC_TRACE_ID_KEY))
                    .title(ERROR_CODE_LOLLIPOP_AUTH);
            try {
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(objectMapper.writeValueAsBytes(problem));
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Flux.just(buffer));
            } catch (JsonProcessingException e) {
                return Mono.error( e );
            }
        }
        return Mono.just("Ok");
    }
}
