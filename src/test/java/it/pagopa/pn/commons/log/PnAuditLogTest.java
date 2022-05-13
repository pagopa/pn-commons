package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static it.pagopa.pn.commons.log.PnAuditLogEventType.*;

class PnAuditLogTest {

    @Test
    void testAuditLog() {

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        PnAuditLog.getLogger().addAppender(listAppender);

        //
        PnAuditLogBuilder auditLogger = new PnAuditLogBuilder();

        // create AuditEvents
        PnAuditLogEvent event1 = auditLogger.before( AUD_NT_ARR, "Test1");

        // call method under test
        event1.log();
        //---- Call to business method
        event1.generateResult(false, "ERROR in calling method").log();


        PnAuditLogEvent event2 = auditLogger.before(
                AUD_ACC_LOGIN,
                "Test format {} = {}",
                "1", "pippo"
            )
            // call method under test
            .log();


        event2.generateSuccess().log();

        // create AuditEvents
        PnAuditLogEvent event3 = auditLogger.before( AUD_NT_ARR, "Test3")
                // call method under test
                .log();
        //---- Call to business method
        event3.generateFailure("ERROR in calling method {}", "pippo").log();

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("AUDIT10Y", logsList.get(0).getMarker().getName());
        assertEquals("INFO", logsList.get(0).getLevel().toString());
        assertTrue(logsList.get(0).getFormattedMessage().startsWith("[AUD_NT_ARR] BEFORE"));
        assertTrue(logsList.get(0).getFormattedMessage().endsWith(" - Test1"));

        assertEquals("AUDIT10Y", logsList.get(1).getMarker().getName());
        assertEquals("ERROR", logsList.get(1).getLevel().toString());
        assertTrue(logsList.get(1).getFormattedMessage().startsWith("[AUD_NT_ARR] FAILURE"));
        assertTrue(logsList.get(1).getFormattedMessage().endsWith(" - ERROR in calling method"));

        assertEquals("AUDIT5Y", logsList.get(2).getMarker().getName());
        assertEquals("INFO", logsList.get(2).getLevel().toString());
        assertTrue(logsList.get(2).getFormattedMessage().startsWith("[AUD_ACC_LOGIN] BEFORE"));
        assertTrue(logsList.get(2).getFormattedMessage().endsWith(" - Test format 1 = pippo"));

        assertEquals("AUDIT5Y", logsList.get(3).getMarker().getName());
        assertEquals("INFO", logsList.get(3).getLevel().toString());
        assertTrue(logsList.get(3).getFormattedMessage().startsWith("[AUD_ACC_LOGIN] SUCCESS"));
        assertTrue(logsList.get(3).getFormattedMessage().endsWith(" - OK"));

        assertEquals("AUDIT10Y", logsList.get(4).getMarker().getName());
        assertEquals("INFO", logsList.get(4).getLevel().toString());
        assertTrue(logsList.get(4).getFormattedMessage().startsWith("[AUD_NT_ARR] BEFORE"));
        assertTrue(logsList.get(4).getFormattedMessage().endsWith(" - Test3"));

        assertEquals("AUDIT10Y", logsList.get(5).getMarker().getName());
        assertEquals("ERROR", logsList.get(5).getLevel().toString());
        assertTrue(logsList.get(5).getFormattedMessage().startsWith("[AUD_NT_ARR] FAILURE"));
        assertTrue(logsList.get(5).getFormattedMessage().endsWith(" - ERROR in calling method pippo"));
    }
}
