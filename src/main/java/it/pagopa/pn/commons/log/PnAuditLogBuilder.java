package it.pagopa.pn.commons.log;

import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PnAuditLogBuilder {
    private PnAuditLogEventType type;
    private String msg;
    private Object[] arguments;
    private Map<String, String> mdcMap;
    private List<GeneralMetric> metricsArray;

    public PnAuditLogBuilder before(PnAuditLogEventType type, String msg, Object ... arguments) {
        this.type = type;
        this.msg = msg;
        this.arguments = arguments;
        this.mdcMap = new HashMap<>();
        return this;
    }

    public PnAuditLogBuilder before(PnAuditLogEventType type, String msg, List<GeneralMetric> metricsArray, Object ... arguments) {
        this.type = type;
        this.msg = msg;
        this.metricsArray = metricsArray;
        this.arguments = arguments;
        this.mdcMap = new HashMap<>();
        return this;
    }

    public PnAuditLogEvent build() {
        if(CollectionUtils.isEmpty(metricsArray)) {
            return new PnAuditLogEvent(type, mdcMap, msg, arguments );
        } else {
            return new PnAuditLogEvent(type, mdcMap, msg, metricsArray, arguments );
        }
    }

    public PnAuditLogBuilder iun(String iun) {
        mdcMap.put(MDCUtils.MDC_PN_IUN_KEY, iun);
        return this;
    }

    public PnAuditLogBuilder mdcEntry(String key, String value) {
        this.mdcMap.put(key, value);
        return this;
    }
}
