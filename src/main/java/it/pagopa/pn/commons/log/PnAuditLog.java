package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        Level level = (Boolean.FALSE.equals( pnAuditLogEvent.getSuccess()) ? Level.ERROR : Level.INFO);

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
                arguments.addAll(List.of( eventArguments ));
            }
            Set<String> mdcKeySet = pnAuditLogEvent.getMdc().keySet();
            try {
                for (String key : mdcKeySet) {
                    MDC.put(key, pnAuditLogEvent.getMdc().get(key));
                }
                MDC.put(AUDIT_TYPE, pnAuditLogEvent.getType().toString());
                String originUuid =  (pnAuditLogEvent.getOriginEvent() == null ? pnAuditLogEvent.getUuid() : pnAuditLogEvent.getOriginEvent().getUuid());
                MDC.put(AUDIT_UUID, originUuid);
                if (Level.INFO.equals(level)) {
                    logger.info(auditLogEventType.marker, format, arguments.toArray());
                } else {
                    logger.error(auditLogEventType.marker, format, arguments.toArray());
                }
            } finally {
                MDC.remove(AUDIT_TYPE);
                MDC.remove(AUDIT_UUID);
                for (String key : mdcKeySet) {
                    MDC.remove(key);
                }
            }
        }
    }

    @NotNull
    private static String computePrefix(PnAuditLogEvent pnAuditLogEvent) {
        String prefix;
        if( pnAuditLogEvent.getOriginEvent() == null ) {
            prefix = "BEFORE";
        }
        else {
            prefix = (pnAuditLogEvent.getSuccess() ? "SUCCESS": "FAILURE");
        }
        return prefix;
    }
}
