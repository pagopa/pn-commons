package it.pagopa.pn.commons.log;


import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

/**
 * This WebFilter reads the request header configured in 'pn.log.trace-id-header' property
 * and put in the MDC log map to use to correlate log between micro-services calls.
 */
public class MDCWebFilter implements WebFilter {

    public static final String MDC_TRACE_ID_KEY = "trace_id";

    @Value("${pn.log.trace-id-header}")
    private String traceIdHeader;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        Runnable mdcSetter = () -> {
            List<String> traceIdHeaders = serverWebExchange.getRequest().getHeaders().get(traceIdHeader);
            if (traceIdHeaders != null) {
                MDC.put(MDC_TRACE_ID_KEY, traceIdHeaders.get(0));
            }
        };

        Consumer mdcCleaner = ignored -> MDC.remove(MDC_TRACE_ID_KEY);

        return webFilterChain.filter(serverWebExchange)
                .doFirst(mdcSetter)
                .doFinally(mdcCleaner);
    }
}