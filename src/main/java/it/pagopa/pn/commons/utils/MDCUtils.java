package it.pagopa.pn.commons.utils;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MDCUtils {

    // nel caso vengano aggiunte altre costanti, aggiornare la lista in getAllMDCKeys più sotto
    public static final String MDC_TRACE_ID_KEY = "trace_id";
    public static final String MDC_JTI_KEY = "jti";
    public static final String MDC_PN_UID_KEY = "uid";
    public static final String MDC_CX_ID_KEY = "cx_id";
    public static final String MDC_PN_CX_TYPE_KEY = "cx_type";
    public static final String MDC_PN_CX_GROUPS_KEY = "cx_groups";
    public static final String MDC_PN_CX_ROLE_KEY = "cx_role";
    public static final String MDC_PN_SOURCE_CHANNEL_KEY = "source_channel";
    public static final String MDC_PN_SOURCE_CHANNEL_DETAILS_KEY = "source_channel_details";

    public static final String MDC_PN_IUN_KEY = "iun";
    public static final String MDC_PN_MANDATEID_KEY = "mandateid";

    public static final String MDC_PN_RECIPIENT_ID_KEY = "recipient_id";
    public static final String MDC_PN_DELEGATOR_ID_KEY = "delegator_id";
    public static final String MDC_PN_DELEGATE_ID_KEY = "delegate_id";
    public static final String MDC_PN_MANDATE_WORKFLOW_TYPE_KEY = "mandate_workflow_type";
    public static final String MDC_PN_MANDATE_CIE_NIS_KEY = "nis";
    public static final String MDC_PN_ERROR_CATEGORY_KEY = "error_category";

    public static final String MDC_PN_CTX_RECIPIENT_INDEX = "ctx_recipient_index";
    public static final String MDC_PN_CTX_REQUEST_ID = "ctx_request_id";
    public static final String MDC_PN_CTX_SAFESTORAGE_FILEKEY = "ctx_safestorage_filekey";
    public static final String MDC_PN_CTX_TOPIC = "ctx_topic";
    public static final String MDC_PN_CTX_MESSAGE_ID = "ctx_awsMessageId";
    public static final String MDC_PN_SET_ID = "set_id";

    public static final String MDC_PN_LP_ORIGINAL_URL = "lollipop_original_url";
    public static final String MDC_PN_LP_ORIGINAL_METHOD = "lollipop_original_method";
    public static final String MDC_PN_LP_PUBLIC_KEY = "lollipop_public_key";
    public static final String MDC_PN_LP_ASSERTION_REF = "lollipop_assertion_ref";
    public static final String MDC_PN_LP_ASSERTION_TYPE = "lollipop_assertion_type";
    public static final String MDC_PN_LP_SIGNATURE_INPUT = "lollipop_signature_input";
    public static final String MDC_PN_LP_SIGNATURE = "lollipop_signature";

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
                , MDC_PN_CX_TYPE_KEY, MDC_PN_CX_GROUPS_KEY, MDC_PN_CX_ROLE_KEY, MDC_PN_SOURCE_CHANNEL_KEY
                , MDC_PN_SOURCE_CHANNEL_DETAILS_KEY, MDC_PN_IUN_KEY, MDC_PN_MANDATEID_KEY, MDC_PN_CTX_RECIPIENT_INDEX
                , MDC_PN_CTX_REQUEST_ID, MDC_PN_CTX_SAFESTORAGE_FILEKEY
                , MDC_PN_CTX_TOPIC, MDC_PN_CTX_MESSAGE_ID, MDC_PN_SET_ID
                , MDC_PN_LP_ORIGINAL_URL, MDC_PN_LP_ORIGINAL_METHOD, MDC_PN_LP_PUBLIC_KEY
                , MDC_PN_LP_ASSERTION_REF, MDC_PN_LP_ASSERTION_TYPE
                , MDC_PN_LP_SIGNATURE_INPUT, MDC_PN_LP_SIGNATURE
                , MDC_PN_RECIPIENT_ID_KEY, MDC_PN_DELEGATOR_ID_KEY, MDC_PN_DELEGATE_ID_KEY
                , MDC_PN_MANDATE_WORKFLOW_TYPE_KEY, MDC_PN_MANDATE_CIE_NIS_KEY, MDC_PN_ERROR_CATEGORY_KEY);
    }

    /**
     * Pulisce le chiavi di pertinenza PN dall'MDC
     */
    public static void clearMDCKeys(){
        getAllMDCKeys().forEach(MDC::remove);
    }


    public static List<MDC.MDCCloseable> alignMDCToWebfluxContext(reactor.util.context.Context context){
        List<MDC.MDCCloseable> closeables = new ArrayList<>();
        getAllMDCKeys().forEach(key -> {
            if (context.hasKey(key))
            {
                closeables.add(MDC.putCloseable(key, context.get(key).toString()));
            }
            else {
                MDC.remove(key);
            }
        });
        return closeables;
    }

    public static <T> Mono<T> addMDCToContextAndExecute(Mono<T> mono) {
        final Map<String, String> fmdc = getMDCMap();

        return Mono.just(fmdc).flatMap(x -> mono)
                .contextWrite(context -> context.putAllMap(fmdc));
    }

    public static <T> Flux<T> addMDCToContextAndExecute(Flux<T> flux) {
        final Map<String, String> fmdc = getMDCMap();

        return Mono.just(fmdc).thenMany(flux)
                .contextWrite(context -> context.putAllMap(fmdc));
    }

    private static Map<String, String> getMDCMap() {
        final Map<String, String> mdc = MDC.getCopyOfContextMap();
        if (mdc == null)
            return new HashMap<>();
        else {
            return mdc.entrySet().stream()
                    .filter(stringStringEntry -> stringStringEntry.getValue() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

}
