package it.pagopa.pn.commons.log;

import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PnAuditLogEvent {
    private PnAuditLogEvent originEvent;
    private final PnAuditLogEventType type;
    private final String message;
    private final Object[] arguments;
    private PnAuditLogType level;
    private final String uuid;
    private final Map<String, String> mdc;
    private List<GeneralMetric> metricsArray;

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
    PnAuditLogType getLevel() {
        return level;
    }
    List<GeneralMetric> getMetricsArray() { return metricsArray; }

    public PnAuditLogEvent(PnAuditLogEventType type, Map<String, String> mdc, String message, Object... arguments) {
        this.type = type;
        this.mdc = mdc;
        this.message = message;
        this.arguments = arguments;
        this.uuid = UUID.randomUUID().toString();
    }

    public PnAuditLogEvent(PnAuditLogEventType type, Map<String, String> mdc, String message, List<GeneralMetric> metricsArray, Object... arguments) {
        this.type = type;
        this.mdc = mdc;
        this.message = message;
        this.metricsArray = metricsArray;
        this.arguments = arguments;
        this.uuid = UUID.randomUUID().toString();
    }

    public PnAuditLogEvent generateSuccess() {
        return generateResult(PnAuditLogType.SUCCESS, this.message, this.arguments);
    }

    public PnAuditLogEvent generateSuccess(String message, Object... arguments) {
        return generateResult(PnAuditLogType.SUCCESS, message, arguments);
    }

    public PnAuditLogEvent generateSuccess(String message, List<GeneralMetric> metricsArray, Object... arguments) {
        return generateResult(PnAuditLogType.SUCCESS, message, metricsArray, arguments);
    }

    public PnAuditLogEvent generateFailure(String message, Object... arguments) {
        return generateResult(PnAuditLogType.FAILURE, message, arguments);
    }

    public PnAuditLogEvent generateFailure(String message, List<GeneralMetric> metricsArray, Object... arguments) {
        return generateResult(PnAuditLogType.FAILURE, message, metricsArray, arguments);
    }

    public PnAuditLogEvent generateWarning(String message, Object... arguments) {
        return generateResult(PnAuditLogType.WARNING, message, arguments);
    }

    public PnAuditLogEvent generateWarning(String message, List<GeneralMetric> metricsArray, Object... arguments) {
        return generateResult(PnAuditLogType.WARNING, message, metricsArray, arguments);
    }
    
    public PnAuditLogEvent generateResult(PnAuditLogType level, String message, Object... arguments) {
        PnAuditLogEvent resultEvent = new PnAuditLogEvent(type, mdc, message, arguments);
        resultEvent.originEvent = this;
        resultEvent.level = level;
        return resultEvent;
    }

    public PnAuditLogEvent generateResult(PnAuditLogType level, String message, List<GeneralMetric> metricsArray, Object... arguments) {
        PnAuditLogEvent resultEvent = new PnAuditLogEvent(type, mdc, message, metricsArray, arguments);
        resultEvent.originEvent = this;
        resultEvent.level = level;
        return resultEvent;
    }

    public PnAuditLogEvent log() {
        PnAuditLog.log(this);
        return this;
    }

    public Map<String, String> getMdc() {
        return mdc;
    }
}
