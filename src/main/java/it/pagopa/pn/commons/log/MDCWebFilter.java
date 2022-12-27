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
import reactor.util.context.Context;

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
    public static final String MDC_CX_ID_KEY = "pn-cx-id";
    public static final String MDC_PN_CX_TYPE_KEY = "pn-cx-type";
    public static final String MDC_PN_CX_GROUPS_KEY = "pn-cx-groups";
    public static final String MDC_PN_CX_ROLE_KEY = "pn-cx-role";

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

    @Value("${pn.log.pn-cx-role-header}")
    private String pnCxRoleHeader;

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
        final List<String> notFoundGeneratedTraceId = List.of("trace_id:" + UUID.randomUUID());
        HttpHeaders requestHeaders = serverWebExchange.getRequest().getHeaders();

        Runnable mdcSetter = () -> {
            List<String> traceIdHeaders = requestHeaders.get(traceIdHeader);
            List<String> traceIdHeaderValues = traceIdHeaders != null ? traceIdHeaders : notFoundGeneratedTraceId;

            addToMDC(traceIdHeaderValues, MDC_TRACE_ID_KEY);
            addToMDC(requestHeaders.get(jtiHeader), MDC_JTI_KEY);
            addToMDC(requestHeaders.get(pnUidHeader), MDC_PN_UID_KEY);
            addToMDC(requestHeaders.get(cxIdHeader), MDC_CX_ID_KEY);
            addToMDC(requestHeaders.get(pnCxTypeHeader), MDC_PN_CX_TYPE_KEY);
            addToMDC(requestHeaders.get(pnCxGroupsHeader), MDC_PN_CX_GROUPS_KEY);
            addToMDC(requestHeaders.get(pnCxRoleHeader), MDC_PN_CX_ROLE_KEY);
        };

        Consumer<SignalType> mdcCleaner = ignored -> {
            MDC.remove(MDC_TRACE_ID_KEY);
            MDC.remove(MDC_JTI_KEY);
            MDC.remove(MDC_PN_UID_KEY);
            MDC.remove(MDC_CX_ID_KEY);
            MDC.remove(MDC_PN_CX_TYPE_KEY);
            MDC.remove(MDC_PN_CX_GROUPS_KEY);
            MDC.remove(MDC_PN_CX_ROLE_KEY);
        };

        return webFilterChain.filter(serverWebExchange)
                .doFirst(mdcSetter)
                .contextWrite(ctx -> enrichContext(ctx, requestHeaders, notFoundGeneratedTraceId))
                .doFinally(mdcCleaner);
    }

    private void addToMDC(List<String> headerValues, String mdcKey) {
        if (headerValues != null) {
            MDC.put(mdcKey, headerValues.get(0));
        }
    }

    private Context enrichContext(Context ctx, HttpHeaders requestHeaders, List<String> notFoundGeneratedTraceId) {
        List<String> traceIdHeaders = requestHeaders.get(traceIdHeader);
        List<String> traceIdHeaderValues = traceIdHeaders != null ? traceIdHeaders : notFoundGeneratedTraceId;

        ctx = addToWebFluxContext(ctx, traceIdHeaderValues, MDC_TRACE_ID_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(jtiHeader), MDC_JTI_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(pnUidHeader), MDC_PN_UID_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(cxIdHeader), MDC_CX_ID_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(pnCxTypeHeader), MDC_PN_CX_TYPE_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(pnCxGroupsHeader), MDC_PN_CX_GROUPS_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(pnCxRoleHeader), MDC_PN_CX_ROLE_KEY);
        return ctx;
    }

    private Context addToWebFluxContext(Context ctx, List<String> headerValues, String mdcKey) {
        if (headerValues != null) {
            ctx = ctx.put(mdcKey, headerValues.get(0));
        }
        return ctx;
    }

}