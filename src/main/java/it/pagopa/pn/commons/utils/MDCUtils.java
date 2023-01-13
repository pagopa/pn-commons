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
}
