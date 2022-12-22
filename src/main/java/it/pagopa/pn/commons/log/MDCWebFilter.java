package it.pagopa.pn.commons.log;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.SignalType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This WebFilter reads the request header configured in 'pn.log.trace-id-header' property
 * and put in the MDC log map to use to correlate log between micro-services calls.
 */
@Slf4j
public class MDCWebFilter implements WebFilter {

    public static final String MDC_TRACE_ID_KEY = "trace_id";
    public static final String MDC_JTI_KEY = "jti";
    public static final String MDC_PN_UID_KEY = "pn-uid";
    public static final String MDC_CX_ID_KEY = "cx-id";
    public static final String MDC_PN_CX_TYPE_KEY = "pn-cx-type";
    public static final String MDC_PN_CX_GROUPS_KEY = "pn-cx-groups";

    @Value("${pn.log.trace-id-header}")
    private String traceIdHeader;

    @Value("${pn.log.jti-header}")
    private String jtiHeader;

    @Value("${pn.log.pn-uid-header}")
    private String pnUidHeader;

    @Value("${pn.log.cx-id-header}")
    private String cxIdHeader;

    @Value("${pn.log.pn-cx-type-header}")
    private String pnCxTypeHeader;

    @Value("${pn.log.pn-cx-groups-header}")
    private String pnCxGroupsHeader;

    private static final String MDC_CONTEXT_REACTOR_KEY = MDCWebFilter.class.getName();

    @PostConstruct
    private void contextOperatorHook() {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY,
                Operators.lift((scannable, coreSubscriber) -> new MdcContextLifter<>(coreSubscriber))
        );
    }

    @PreDestroy
    private void cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY);
    }

    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        final String notFoundGeneratedTraceId = "trace_id:" + UUID.randomUUID();
        Runnable mdcSetter = () -> {
            HttpHeaders requestHeaders = serverWebExchange.getRequest().getHeaders();
            List<String> traceIdHeaders = requestHeaders.get(traceIdHeader);

            if (traceIdHeaders != null) {
                MDC.put(MDC_TRACE_ID_KEY, traceIdHeaders.get(0));
            }
            else
            {

                log.debug("trace id header not found, generating internal trace_id={}", notFoundGeneratedTraceId);
                MDC.put(MDC_TRACE_ID_KEY, notFoundGeneratedTraceId);
            }

            List<String> jtiIdHeaders = requestHeaders.get(jtiHeader);
            if (jtiIdHeaders != null) {
                MDC.put(MDC_JTI_KEY, jtiIdHeaders.get(0));
            }
            List<String> uidHeaders = requestHeaders.get(pnUidHeader);
            if(uidHeaders != null) {
                MDC.put(MDC_PN_UID_KEY, uidHeaders.get(0));
            }
            List<String> cxIdHeaders = requestHeaders.get(cxIdHeader);
            if(cxIdHeaders != null) {
                MDC.put(MDC_CX_ID_KEY, cxIdHeaders.get(0));
            }
            List<String> pnCxTypeHeaders = requestHeaders.get(pnCxTypeHeader);
            if(pnCxTypeHeaders != null) {
                MDC.put(MDC_PN_CX_TYPE_KEY, pnCxTypeHeaders.get(0));
            }
            List<String> pnCxGroupsHeaders = requestHeaders.get(pnCxGroupsHeader);
            if(pnCxGroupsHeaders != null) {
                MDC.put(MDC_PN_CX_GROUPS_KEY, pnCxGroupsHeaders.get(0));
            }
        };

        Consumer<SignalType> mdcCleaner = ignored -> {
            MDC.remove(MDC_TRACE_ID_KEY);
            MDC.remove(MDC_JTI_KEY);
            MDC.remove(MDC_PN_UID_KEY);
            MDC.remove(MDC_CX_ID_KEY);
            MDC.remove(MDC_PN_CX_TYPE_KEY);
            MDC.remove(MDC_PN_CX_GROUPS_KEY);
        };

        return webFilterChain.filter(serverWebExchange)
                .doFirst(mdcSetter)
                .contextWrite(ctx -> {
                    HttpHeaders requestHeaders = serverWebExchange.getRequest().getHeaders();
                    List<String> jtiIdHeaders = requestHeaders.get(jtiHeader);
                    if (jtiIdHeaders != null) {
                        ctx = ctx.put(MDC_JTI_KEY, jtiIdHeaders.get(0));
                    }
                    List<String> uidHeaders = requestHeaders.get(pnUidHeader);
                    if(uidHeaders != null) {
                        ctx = ctx.put(MDC_PN_UID_KEY, uidHeaders.get(0));
                    }
                    List<String> cxIdHeaders = requestHeaders.get(cxIdHeader);
                    if(cxIdHeaders != null) {
                        ctx = ctx.put(MDC_CX_ID_KEY, cxIdHeaders.get(0));
                    }
                    List<String> pnCxTypeHeaders = requestHeaders.get(pnCxTypeHeader);
                    if(pnCxTypeHeaders != null) {
                        ctx = ctx.put(MDC_PN_CX_TYPE_KEY, pnCxTypeHeaders.get(0));
                    }
                    List<String> pnCxGroupsHeaders = requestHeaders.get(pnCxGroupsHeader);
                    if(pnCxGroupsHeaders != null) {
                        ctx = ctx.put(MDC_PN_CX_GROUPS_KEY, pnCxGroupsHeaders.get(0));
                    }
                    List<String> traceIdHeaders = requestHeaders.get(traceIdHeader);
                    if (traceIdHeaders != null) {
                        return ctx.put(MDC_TRACE_ID_KEY, traceIdHeaders.get(0));
                    }
                    else
                        return ctx.put(MDC_TRACE_ID_KEY, notFoundGeneratedTraceId);
                }).doFinally(mdcCleaner)
                ;
    }
}