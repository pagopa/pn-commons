package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class PnAuditLog {

    private PnAuditLog() {
        throw new UnsupportedOperationException();
    }

    static final Logger logger = (Logger) LoggerFactory.getLogger(PnAuditLog.class);

    public static void logBefore(PnAuditLogEvent pnAuditLogEvent) {
        log(pnAuditLogEvent, "Before", true);
    }

    public static void logAfterSuccess(PnAuditLogEvent pnAuditLogEvent) {
        logAfter(pnAuditLogEvent, true);
    }
    public static void logAfterFailure(PnAuditLogEvent pnAuditLogEvent) {
        logAfter(pnAuditLogEvent,  false);
    }
    public static void logAfter(PnAuditLogEvent pnAuditLogEvent, boolean success) {
        log(pnAuditLogEvent, "After", success);
    }

    private static void log(PnAuditLogEvent pnAuditLogEvent, String prefix, boolean success) {
        if (logger.isInfoEnabled()) {
            StringBuilder format = new StringBuilder()
                    .append("[{}] - {} - ")
                    .append(pnAuditLogEvent.format);
            int argumentsLength = (pnAuditLogEvent.arguments == null ? 2 : pnAuditLogEvent.arguments.length +2);
            Object[] arguments = new Object[argumentsLength];
            arguments[0] = pnAuditLogEvent.type.toString();
            arguments[1] = prefix;
            if ((pnAuditLogEvent.arguments != null) && (pnAuditLogEvent.arguments.length > 0)) {
                System.arraycopy(pnAuditLogEvent.arguments, 0, arguments, 2, pnAuditLogEvent.arguments.length);
            }
            if (success) {
                logger.info(pnAuditLogEvent.type.marker, format.toString(), arguments);
            } else {
                logger.error(pnAuditLogEvent.type.marker, format.toString(), arguments);
            }
        }
    }

}
