package it.pagopa.pn.commons.log;

import java.util.UUID;

public class PnAuditLogEvent {
    PnAuditLogEvent originEvent;
    PnAuditLogEventType type;
    String message;
    Object[] arguments;
    boolean success = true;
    UUID uuid;

    public PnAuditLogEvent(PnAuditLogEventType type, String message) {
        this.type = type;
        this.message = message;
        this.uuid = UUID.randomUUID();
    }

    public PnAuditLogEvent(PnAuditLogEventType type, String message, Object... arguments) {
        this.type = type;
        this.message = message;
        this.arguments = arguments;
        this.uuid = UUID.randomUUID();
    }
    public PnAuditLogEvent generateResultSuccess() {
        return generateResult(true, this.message);
    }

    public PnAuditLogEvent generateResultFailure(String message) {
        return generateResult(false, message);
    }

    public PnAuditLogEvent generateResult(boolean success, String message) {
        PnAuditLogEvent resultEvent = new PnAuditLogEvent(type, message);
        resultEvent.originEvent = this;
        resultEvent.success = success;
        resultEvent.arguments = this.arguments;
        return resultEvent;
    }

    public PnAuditLogEvent generateResult(boolean success, String message, Object... arguments) {
        PnAuditLogEvent resultEvent = new PnAuditLogEvent(type, message, arguments);
        resultEvent.originEvent = this;
        resultEvent.success = success;
        return resultEvent;
    }

    public void log() {
        PnAuditLog.log(this);
    }
    
}
