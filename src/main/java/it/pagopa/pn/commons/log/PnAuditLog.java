package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

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

        Level level = (Boolean.FALSE.equals( pnAuditLogEvent.getSuccess()) ? Level.ERROR : Level.INFO);

        Logger logger = LazyHolder.INSTANCE;

        if (logger.isEnabledFor(level)) {

            // - Prepara messaggio
            String format = "[{}] {} {}<-{} - " + pnAuditLogEvent.getMessage();

            // - Prepara parametri del messaggio
            Object[] eventArguments = pnAuditLogEvent.getArguments();
            int eventArgumentsLength = ( eventArguments == null ? 0 : eventArguments.length);
            ArrayList<Object> arguments = new ArrayList<>( eventArgumentsLength + 4);

            // Event Type
            PnAuditLogEventType auditLogEventType = pnAuditLogEvent.getType();
            arguments.add( auditLogEventType.toString() );

            // prefix
            String prefix = computePrefix( pnAuditLogEvent );
            arguments.add( prefix );

            // uuid
            arguments.add( pnAuditLogEvent.getUuid() );

            // origin reference
            PnAuditLogEvent originEvent = pnAuditLogEvent.getOriginEvent();
            String originString =  originEvent == null ? "origin" : originEvent.getUuid();
            arguments.add( originString );

            // event specific arguments
            if ( eventArguments != null ) {
                arguments.addAll(List.of( eventArguments ));
            }

            if ( Level.INFO.equals( level )) {
                logger.info( auditLogEventType.marker, format, arguments.toArray());
            } else {
                logger.error( auditLogEventType.marker, format, arguments.toArray());
            }
        }
    }

    @NotNull
    private static String computePrefix(PnAuditLogEvent pnAuditLogEvent) {
        String prefix;
        if( pnAuditLogEvent.getOriginEvent() == null ) {
            prefix = "BEFORE";
        }
        else if(pnAuditLogEvent.getSuccess()) {
            prefix = "SUCCESS";
        } else {
            prefix = "FAILURE";
        }
        return prefix;
    }
}
