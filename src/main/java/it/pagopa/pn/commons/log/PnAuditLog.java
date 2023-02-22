package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static ch.qos.logback.classic.Level.*;
import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_AUDIT_LOG_LEVEL_NOT_SPECIFIED;
import static it.pagopa.pn.commons.log.PnAuditLogType.WARNING;

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
                for (String key : mdcKeySet) {
                    MDC.remove(key);
                }
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
            if(ERROR.equals(pnAuditLogEvent.getLevel())){
                level = ERROR;
            }else if(WARNING.equals(pnAuditLogEvent.getLevel())){
                level = WARN;
            }
        }
        return level;
    }

    @NotNull
    private static String computePrefix(PnAuditLogEvent pnAuditLogEvent) {
        if( pnAuditLogEvent.getOriginEvent() == null ) {
            return  "BEFORE";
        }
        else {
            switch (pnAuditLogEvent.getLevel()) {
                case FAILURE -> {
                    return "FAILURE";
                }
                case WARNING -> {
                    return "WARNING";
                }
                case SUCCESS -> {
                    return "SUCCESS";
                }
                default -> throw new PnInternalException("AuditLog level not found", ERROR_CODE_AUDIT_LOG_LEVEL_NOT_SPECIFIED);
            }
        }
    }
}
