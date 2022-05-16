package it.pagopa.pn.commons.log;

import org.springframework.stereotype.Component;

@Component
public class PnAuditLogBuilder {

    public PnAuditLogEvent before(PnAuditLogEventType type, String msg, Object ... arguments) {
        return new PnAuditLogEvent( type, msg, arguments );
    }

}
