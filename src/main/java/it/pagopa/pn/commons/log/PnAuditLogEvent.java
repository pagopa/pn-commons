package it.pagopa.pn.commons.log;

import lombok.Builder;
import lombok.With;

@Builder
public class PnAuditLogEvent {
    PnAuditLogEventType type;
    @With
    String format;
    @With
    Object[] arguments;

    public PnAuditLogEvent(PnAuditLogEventType type, String message) {
        this.type = type;
        this.format = message;
    }

    public PnAuditLogEvent(PnAuditLogEventType type, String format, Object... arguments) {
        this.type = type;
        this.format = format;
        this.arguments = arguments;
    }

}
