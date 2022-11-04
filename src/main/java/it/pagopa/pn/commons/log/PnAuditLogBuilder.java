package it.pagopa.pn.commons.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PnAuditLogBuilder {
    private PnAuditLogEventType type;
    private String msg;
    private Object[] arguments;
    private Map<String, String> mdcMap;

    public PnAuditLogBuilder before(PnAuditLogEventType type, String msg, Object ... arguments) {
        this.type = type;
        this.msg = msg;
        this.arguments = arguments;
        this.mdcMap = new HashMap<>();
        return this;
    }

    public PnAuditLogEvent build() {
        return new PnAuditLogEvent(type, mdcMap, msg, arguments );
    }

    public PnAuditLogBuilder iun(String iun) {
        mdcMap.put("iun", iun);
        return this;
    }

    public PnAuditLogBuilder uid(String uid) {
        mdcMap.put("uid", uid);
        return this;
    }

    public PnAuditLogBuilder cxId(String cxId) {
        mdcMap.put("cx_id", cxId);
        return this;
    }

    public PnAuditLogBuilder cxType(String cxType) {
        mdcMap.put("cx_type", cxType);
        return this;
    }

    public PnAuditLogBuilder mdcEntry(String key, String value) {
        this.mdcMap.put(key, value);
        return this;
    }
}
