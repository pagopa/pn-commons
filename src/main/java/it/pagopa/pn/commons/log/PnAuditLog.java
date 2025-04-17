package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.utils.MetricUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static ch.qos.logback.classic.Level.*;
import static net.logstash.logback.marker.Markers.appendEntries;
import static net.logstash.logback.marker.Markers.appendRaw;

public class PnAuditLog {

    public static final String AUDIT_TYPE = "aud_type";
    public static final String AUDIT_UUID = "aud_orig";

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

        Level level = getLevel(pnAuditLogEvent);

        Logger logger = LazyHolder.INSTANCE;

        if (logger.isEnabledFor(level)) {

            // - Prepara messaggio
            String format = "[{}] {} - " + pnAuditLogEvent.getMessage();

            // - Prepara parametri del messaggio
            Object[] eventArguments = pnAuditLogEvent.getArguments();
            int eventArgumentsLength = ( eventArguments == null ? 0 : eventArguments.length);
            ArrayList<Object> arguments = new ArrayList<>( eventArgumentsLength + 2);

            // Event Type
            PnAuditLogEventType auditLogEventType = pnAuditLogEvent.getType();
            arguments.add( auditLogEventType.toString() );

            // prefix
            String prefix = computePrefix( pnAuditLogEvent );
            arguments.add( prefix );

            // event specific arguments
            if ( eventArguments != null ) {
                arguments.addAll( Arrays.asList(eventArguments) );
            }

            MetricUtils.generateMetricsLog(logger, pnAuditLogEvent.getMetricsArray(), pnAuditLogEvent.getMetricFormatType());

            Set<String> mdcKeySet = pnAuditLogEvent.getMdc().keySet();
            try {
                for (String key : mdcKeySet) {
                    MDC.put(key, pnAuditLogEvent.getMdc().get(key));
                }
                MDC.put(AUDIT_TYPE, pnAuditLogEvent.getType().toString());
                String originUuid =  (pnAuditLogEvent.getOriginEvent() == null ? pnAuditLogEvent.getUuid() : pnAuditLogEvent.getOriginEvent().getUuid());
                MDC.put(AUDIT_UUID, originUuid);

                setLogger(level, logger, format, arguments, auditLogEventType);

            } finally {
                MDC.remove(AUDIT_TYPE);
                MDC.remove(AUDIT_UUID);
                // Non vengono più rimosse le altre eventuali chiavi, dato che sono in comune con quelle normalmente presenti nei log
            }
        }
    }

    private static void setLogger(Level level, Logger logger, String format, ArrayList<Object> arguments, PnAuditLogEventType auditLogEventType) {
        if (WARN.equals(level)) {
            logger.warn(auditLogEventType.marker, format, arguments.toArray());
        } else {
            if (ERROR.equals(level)) {
                logger.error(auditLogEventType.marker, format, arguments.toArray());
            } else {
                logger.info(auditLogEventType.marker, format, arguments.toArray());
            }
        }
    }

    @NotNull
    private static Level getLevel(PnAuditLogEvent pnAuditLogEvent) {
        Level level = INFO;

        if (pnAuditLogEvent.getLevel() != null){
            level = switch (pnAuditLogEvent.getLevel()) {
                case FAILURE -> ERROR;
                case WARNING -> WARN;
                case SUCCESS -> INFO;
            };
        }
        
        return level;
    }

    @NotNull
    private static String computePrefix(PnAuditLogEvent pnAuditLogEvent) {
        String prefix;
        if( pnAuditLogEvent.getOriginEvent() == null ) {
            prefix = "BEFORE";
        }
        else {
            prefix = switch (pnAuditLogEvent.getLevel()) {
                case FAILURE -> "FAILURE";
                case WARNING -> "WARNING";
                case SUCCESS -> "SUCCESS";
            };
        }
        return prefix;
    }
}
