package it.pagopa.pn.commons.utils;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class MDCUtils {

    public static final String MDC_PN_CTX_IUN = "ctx_iun";
    public static final String MDC_PN_CTX_MANDATEID = "ctx_mandate_id";
    public static final String MDC_PN_CTX_RECIPIENT_INDEX = "ctx_recipient_index";
    public static final String MDC_PN_CTX_REQUEST_ID = "ctx_request_id";
    public static final String MDC_PN_CTX_TOPIC = "ctx_topic";
    public static final String MDC_PN_CTX_MESSAGE_ID = "ctx_awsMessageId";

    private MDCUtils() {}

    public static Map<String, String> retrieveMDCContextMap() {
        return MDC.getCopyOfContextMap();
    }

    public static <V> V enrichWithMDC(V t, Map<String, String> copyOfContextMap) {
        if(! CollectionUtils.isEmpty(copyOfContextMap)) {
            MDC.setContextMap(copyOfContextMap);
        }
        return t;
    }


    public static <T> Mono<T> addMDCToContextAndExecute(Mono<T> mono) {
        final Map<String, String> mdc = MDC.getCopyOfContextMap();
        return Mono.just(mdc).flatMap(x -> mono)
                .contextWrite(context -> context.putAllMap(mdc));
    }


    public static <T> Flux<T> addMDCToContextAndExecute(Flux<T> flux) {
        final Map<String, String> mdc = MDC.getCopyOfContextMap();
        return Mono.just(mdc).thenMany(flux)
                .contextWrite(context -> context.putAllMap(mdc));
    }
}
