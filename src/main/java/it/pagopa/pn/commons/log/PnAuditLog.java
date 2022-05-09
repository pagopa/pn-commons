package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class PnAuditLog {

    private PnAuditLog() {
        throw new UnsupportedOperationException();
    }

    static final Logger logger = (Logger) LoggerFactory.getLogger(PnAuditLog.class);

    static void info(PnAuditLogEvent pnAuditLogEvent) {
        if (pnAuditLogEvent.format == null) {
            logger.info(pnAuditLogEvent.type.marker, pnAuditLogEvent.message);
        } else {
            logger.info(pnAuditLogEvent.type.marker, pnAuditLogEvent.format, pnAuditLogEvent.arguments);
        }
    }

}
