package it.pagopa.pn.commons.pnclients.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.national.registries.utils.MaskTaxIdInPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DownstreamCallLoggingFilter implements ExchangeFilterFunction {

    private static final Logger DOWNSTREAM_LOG = LoggerFactory.getLogger("DownstreamCallLogger");

    private final String clientName;
    private final ObjectMapper objectMapper;

    DownstreamCallLoggingFilter(String clientName, ObjectMapper objectMapper) {
        this.clientName = clientName;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        long startNano = System.nanoTime();
        Instant timestamp = Instant.now();
        String httpMethod = request.method().name();
        String rawUrl = request.url().toString();
        String maskedUrl = MaskTaxIdInPathUtils.maskTaxIdInPath(rawUrl);
        String host = request.url().getHost();
        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");

        return next.exchange(request)
                .doOnSuccess(response -> {
                    long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
                    int status = response.statusCode().value();
                    boolean success = response.statusCode().is2xxSuccessful();
                    String outcome = resolveOutcome(response);
                    log(timestamp, httpMethod, maskedUrl, host, status, success, outcome, durationMs,
                            traceId, spanId, null, null);
                })
                .doOnError(throwable -> {
                    long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
                    int status = extractStatus(throwable);
                    String outcome = "ERROR";
                    String errorMessage = MaskTaxIdInPathUtils.maskTaxIdInPath(
                            throwable.getMessage() != null ? throwable.getMessage() : "unknown");
                    log(timestamp, httpMethod, maskedUrl, host, status, false, outcome, durationMs,
                            traceId, spanId, throwable.getClass().getSimpleName(), errorMessage);
                });
    }

    private String resolveOutcome(ClientResponse response) {
        int statusCode = response.statusCode().value();
        if (response.statusCode().is2xxSuccessful()) return "SUCCESS";
        if (statusCode >= 400 && statusCode < 500) return "CLIENT_ERROR";
        if (statusCode >= 500) return "SERVER_ERROR";
        return "SUCCESS";
    }

    private int extractStatus(Throwable throwable) {
        if (throwable instanceof WebClientResponseException ex) {
            return ex.getStatusCode().value();
        }
        return -1;
    }

    private void log(Instant timestamp, String httpMethod, String url, String host,
                     int status, boolean success, String outcome, long durationMs,
                     String traceId, String spanId, String errorType, String errorMessage) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", "downstream_http_call");
            event.put("timestamp", timestamp.toString());
            event.put("clientName", clientName);
            event.put("method", httpMethod);
            event.put("url", url);
            event.put("host", host);
            event.put("status", status);
            event.put("success", success);
            event.put("outcome", outcome);
            event.put("durationMs", durationMs);
            event.put("traceId", traceId);
            event.put("spanId", spanId);
            event.put("errorType", errorType);
            event.put("errorMessage", errorMessage);
            DOWNSTREAM_LOG.info(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            DOWNSTREAM_LOG.warn("Failed to serialize downstream call event for client {}", clientName, e);
        }
    }
}