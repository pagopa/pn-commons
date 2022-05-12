package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PnAuditLog {

    private PnAuditLog() {
        throw new UnsupportedOperationException();
    }

    private static class LazyHolder {
        public static final Logger INSTANCE = (Logger) LoggerFactory.getLogger(PnAuditLog.class);
    }
    static Logger getLogger() {
        return LazyHolder.INSTANCE;
    }
    static void log(PnAuditLogEvent pnAuditLogEvent) {
        String prefix = (pnAuditLogEvent.originEvent == null ? "BEFORE" : "RESULT");
        Level level = (pnAuditLogEvent.success ? Level.INFO : Level.ERROR);
        Logger logger = LazyHolder.INSTANCE;
        if (logger.isEnabledFor(level)) {
            StringBuilder format = new StringBuilder()
                    .append("[{}] {} {}<-{} - ")
                    .append(pnAuditLogEvent.message);
            ArrayList<Object> arguments = new ArrayList<>(pnAuditLogEvent.arguments == null ? 4 : pnAuditLogEvent.arguments.length + 4);
            arguments.add(pnAuditLogEvent.type.toString());
            arguments.add(prefix);
            arguments.add(pnAuditLogEvent.uuid.toString());
            arguments.add(pnAuditLogEvent.originEvent == null ? "origin" : pnAuditLogEvent.originEvent.uuid.toString());
            if ((pnAuditLogEvent.arguments != null) && (pnAuditLogEvent.arguments.length > 0)) {
                arguments.addAll(List.of(pnAuditLogEvent.arguments));
            }
            if (pnAuditLogEvent.success) {
                logger.info(pnAuditLogEvent.type.marker, format.toString(), arguments.toArray());
            } else {
                logger.error(pnAuditLogEvent.type.marker, format.toString(), arguments.toArray());
            }
        }
    }
}
