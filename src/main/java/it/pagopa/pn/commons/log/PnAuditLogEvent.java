package it.pagopa.pn.commons.log;

import java.util.UUID;

public class PnAuditLogEvent {
    private PnAuditLogEvent originEvent;
    private final PnAuditLogEventType type;
    private final String message;
    private final Object[] arguments;
    private Boolean success;
    private final String uuid;

    Object[] getArguments() {
        return arguments;
    }
    PnAuditLogEventType getType() {
        return type;
    }
    PnAuditLogEvent getOriginEvent() {
        return originEvent;
    }
    String getMessage() {
        return message;
    }
    String getUuid() {
        return uuid;
    }
    boolean getSuccess() {
        return (success==null) || (success);
    }

    public PnAuditLogEvent(PnAuditLogEventType type, String message, Object... arguments) {
        this.type = type;
        this.message = message;
        this.arguments = arguments;
        this.uuid = UUID.randomUUID().toString();
    }

    public PnAuditLogEvent generateSuccess() {
        return generateResult(true, "OK");
    }

    public PnAuditLogEvent generateSuccess(String message, Object... arguments) {
        return generateResult(true, message, arguments);
    }

    public PnAuditLogEvent generateFailure(String message, Object... arguments) {
        return generateResult(false, message, arguments);
    }

    public PnAuditLogEvent generateResult(boolean success, String message, Object... arguments) {
        PnAuditLogEvent resultEvent = new PnAuditLogEvent(type, message, arguments);
        resultEvent.originEvent = this;
        resultEvent.success = success;
        return resultEvent;
    }

    public PnAuditLogEvent log() {
        PnAuditLog.log(this);
        return this;
    }

}
