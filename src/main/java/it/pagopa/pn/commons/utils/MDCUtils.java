package it.pagopa.pn.commons.utils;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class MDCUtils {

    // nel caso vengano aggiunte altre costanti, aggiornare la lista in getAllMDCKeys più sotto
    public static final String MDC_TRACE_ID_KEY = "trace_id";
    public static final String MDC_JTI_KEY = "jti";
    public static final String MDC_PN_UID_KEY = "uid";
    public static final String MDC_CX_ID_KEY = "cx_id";
    public static final String MDC_PN_CX_TYPE_KEY = "cx_type";
    public static final String MDC_PN_CX_GROUPS_KEY = "cx_groups";
    public static final String MDC_PN_CX_ROLE_KEY = "cx_role";

    public static final String MDC_PN_IUN_KEY = "iun";
    public static final String MDC_PN_MANDATEID_KEY = "mandateid";

    public static final String MDC_PN_CTX_RECIPIENT_INDEX = "ctx_recipient_index";
    public static final String MDC_PN_CTX_REQUEST_ID = "ctx_request_id";
    public static final String MDC_PN_CTX_SAFESTORAGE_FILEKEY = "ctx_safestorage_filekey";
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

    public static List<String> getAllMDCKeys() {
        return List.of(MDC_TRACE_ID_KEY, MDC_JTI_KEY, MDC_PN_UID_KEY, MDC_CX_ID_KEY
                , MDC_PN_CX_TYPE_KEY, MDC_PN_CX_GROUPS_KEY, MDC_PN_CX_ROLE_KEY, MDC_PN_IUN_KEY
                , MDC_PN_MANDATEID_KEY, MDC_PN_CTX_RECIPIENT_INDEX, MDC_PN_CTX_REQUEST_ID, MDC_PN_CTX_SAFESTORAGE_FILEKEY
                , MDC_PN_CTX_TOPIC, MDC_PN_CTX_MESSAGE_ID);
    }

    /**
     * Pulisce le chiavi di pertinenza PN dall'MDC
     */
    public static void clearMDCKeys(){
        getAllMDCKeys().forEach(MDC::remove);
    }


    public static void alignMDCToWebfluxContext( reactor.util.context.Context context){
        getAllMDCKeys().forEach(key -> {
            if (context.hasKey(key))
            {
                MDC.putCloseable(key, context.get(key).toString());
            }
            else {
                MDC.remove(key);
            }
        });
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
