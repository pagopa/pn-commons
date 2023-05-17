package it.pagopa.pn.commons.utils;

import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public class MDCUtils {

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

    public static void setIUN(String iun){
        MDC.put("ctx_iun", iun);
    }

    public static void setInternalId(String internalId){
        MDC.put("ctx_internal_id", internalId);
    }

    public static void setUid(String uid){
        MDC.put("ctx_uid", uid);
    }

    public static void setMandateid(String mandateid){
        MDC.put("ctx_mandate_id", mandateid);
    }

    public static void setRecipientIndex(int recipientIndex){
        MDC.put("ctx_recipient_index", recipientIndex+"");
    }

    public static void setRequestId(String requestId){
        MDC.put("ctx_request_id", requestId);
    }

    public static void setTopic(String topic){
        MDC.put("ctx_topic", topic);
    }

    /**
     * aggiunge all'MDC una coppia chiave valore
     * @param key chiave da aggiungere, alla quale verrà aggiunto automaticamente il prefisso ctx_
     * @param value valore da associare alla chiave
     */
    public static void setCustom(String key, String value){
        MDC.put("ctx_" + key, value);
    }
}
