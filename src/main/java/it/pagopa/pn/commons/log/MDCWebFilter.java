package it.pagopa.pn.commons.log;


import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
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

import static it.pagopa.pn.commons.lollipop.LollipopHeaders.*;
import static it.pagopa.pn.commons.utils.MDCUtils.*;

/**
 * This WebFilter reads the request header configured in 'pn.log.trace-id-header' property
 * and put in the MDC log map to use to correlate log between micro-services calls.
 */
@Slf4j
public class MDCWebFilter implements OrderedWebFilter {


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

    @Value("${pn.log.pn-source-channel-header}")
    private String pnSourceChannelHeader;

    @Value("${pn.log.pn-source-channel-details-header}")
    private String pnSourceChannelDetailsHeader;

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
            addToMDC(requestHeaders.get(pnSourceChannelHeader), MDC_PN_SOURCE_CHANNEL_KEY);
            addToMDC(requestHeaders.get(pnSourceChannelDetailsHeader), MDC_PN_SOURCE_CHANNEL_DETAILS_KEY);
            addToMDC(requestHeaders.get(LOLLIPOP_ORIGINAL_URL), MDC_PN_LP_ORIGINAL_URL);
            addToMDC(requestHeaders.get(LOLLIPOP_ORIGINAL_METHOD), MDC_PN_LP_ORIGINAL_METHOD);
            addToMDC(requestHeaders.get(LOLLIPOP_PUBLIC_KEY), MDC_PN_LP_PUBLIC_KEY);
            addToMDC(requestHeaders.get(LOLLIPOP_ASSERTION_REF), MDC_PN_LP_ASSERTION_REF);
            addToMDC(requestHeaders.get(LOLLIPOP_ASSERTION_TYPE), MDC_PN_LP_ASSERTION_TYPE);
            addToMDC(requestHeaders.get(LOLLIPOP_SIGNATURE_INPUT), MDC_PN_LP_SIGNATURE_INPUT);
            addToMDC(requestHeaders.get(LOLLIPOP_SIGNATURE), MDC_PN_LP_SIGNATURE);
        };

        Consumer<SignalType> mdcCleaner = ignored -> MDCUtils.clearMDCKeys();


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
        ctx = addToWebFluxContext(ctx, requestHeaders.get(pnSourceChannelHeader), MDC_PN_SOURCE_CHANNEL_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(pnSourceChannelDetailsHeader), MDC_PN_SOURCE_CHANNEL_DETAILS_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_ORIGINAL_URL), MDC_PN_LP_ORIGINAL_URL);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_ORIGINAL_METHOD), MDC_PN_LP_ORIGINAL_METHOD);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_PUBLIC_KEY), MDC_PN_LP_PUBLIC_KEY);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_ASSERTION_REF), MDC_PN_LP_ASSERTION_REF);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_ASSERTION_TYPE), MDC_PN_LP_ASSERTION_TYPE);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_SIGNATURE_INPUT), MDC_PN_LP_SIGNATURE_INPUT);
        ctx = addToWebFluxContext(ctx, requestHeaders.get(LOLLIPOP_SIGNATURE), MDC_PN_LP_SIGNATURE);
        return ctx;
    }

    private Context addToWebFluxContext(Context ctx, List<String> headerValues, String mdcKey) {
        if (headerValues != null) {
            ctx = ctx.put(mdcKey, headerValues.get(0));
        }
        return ctx;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}