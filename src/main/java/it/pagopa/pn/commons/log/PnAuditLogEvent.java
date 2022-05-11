package it.pagopa.pn.commons.log;

import lombok.Builder;
import lombok.With;

@Builder
public class PnAuditLogEvent {
    PnAuditLogEventType type;
    String message;
    String format;
    Object[] arguments;
    @With
    boolean failure;

    public PnAuditLogEvent(PnAuditLogEventType type, String message) {
        this.type = type;
        this.message = message;
    }

    public PnAuditLogEvent(PnAuditLogEventType type, String format, Object... arguments) {
        this.type = type;
        this.format = format;
        this.arguments = arguments;
    }
}
