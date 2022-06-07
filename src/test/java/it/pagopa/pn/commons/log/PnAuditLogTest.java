package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static it.pagopa.pn.commons.log.PnAuditLog.AUDIT_TYPE;
import static it.pagopa.pn.commons.log.PnAuditLogEventType.*;
import static org.junit.jupiter.api.Assertions.*;

class PnAuditLogTest {
    ListAppender<ILoggingEvent> listAppender;
    PnAuditLogBuilder auditLogger;

    @BeforeEach
    void setupLog() {
        listAppender = new ListAppender<>();
        // create and start a ListAppender
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        PnAuditLog.getLogger().addAppender(listAppender);
        auditLogger = new PnAuditLogBuilder();
    }

    @Test
    void testAuditLog1() {
        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_ARR, "Test1").iun("CNZS-RZBB-HJAT-202205-E-1").build();

        // call method under test
        logEvent.log();
        //---- Call to business method
        logEvent.generateResult(false, "ERROR in calling method").log();
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent1 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent1.getMarker().getName());
        assertEquals("INFO", loggingEvent1.getLevel().toString());
        assertEquals("AUD_NT_ARR", loggingEvent1.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent1.getFormattedMessage().startsWith("[AUD_NT_ARR] BEFORE"));
        assertTrue(loggingEvent1.getFormattedMessage().endsWith(" - Test1"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1", logEvent.getMdc().get("iun"));


        final ILoggingEvent loggingEvent2 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent2.getMarker().getName());
        assertEquals("ERROR", loggingEvent2.getLevel().toString());
        assertEquals("AUD_NT_ARR", loggingEvent2.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent2.getFormattedMessage().startsWith("[AUD_NT_ARR] FAILURE"));
        assertTrue(loggingEvent2.getFormattedMessage().endsWith(" - ERROR in calling method"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1",logEvent.getMdc().get("iun"));
    }

    @Test
    void testAuditLog2() {
        PnAuditLogEvent logEvent = auditLogger.before(
                        AUD_ACC_LOGIN,
                        "Test format {} = {}",
                        "1", "pippo"
                ).mdcEntry("CUSTOM_MDC", "CUSTOM_VALUE")
                .build()
                // call method under test
                .log();


        logEvent.generateSuccess().log();

        List<ILoggingEvent> logsList = listAppender.list;

        final ILoggingEvent loggingEvent3 = logsList.get(0);
        assertEquals("AUDIT5Y", loggingEvent3.getMarker().getName());
        assertEquals("INFO", loggingEvent3.getLevel().toString());
        assertEquals("AUD_ACC_LOGIN", loggingEvent3.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent3.getFormattedMessage().startsWith("[AUD_ACC_LOGIN] BEFORE"));
        assertTrue(loggingEvent3.getFormattedMessage().endsWith(" - Test format 1 = pippo"));
        assertEquals("CUSTOM_VALUE",logEvent.getMdc().get("CUSTOM_MDC"));

        final ILoggingEvent loggingEvent4 = logsList.get(1);
        assertEquals("AUDIT5Y", loggingEvent4.getMarker().getName());
        assertEquals("INFO", loggingEvent4.getLevel().toString());
        assertEquals("AUD_ACC_LOGIN", loggingEvent4.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent4.getFormattedMessage().startsWith("[AUD_ACC_LOGIN] SUCCESS"));
        assertTrue(loggingEvent4.getFormattedMessage().endsWith(" - OK"));
        assertEquals("CUSTOM_VALUE",logEvent.getMdc().get("CUSTOM_MDC"));
    }

    @Test
    void testAuditLog3() {
        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_ARR, "Test3")
                .build()
                // call method under test
                .log();
        //---- Call to business method
        logEvent.generateFailure("ERROR in calling method {}", "pippo").log();

        List<ILoggingEvent> logsList = listAppender.list;

        final ILoggingEvent loggingEvent5 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent5.getMarker().getName());
        assertEquals("INFO", loggingEvent5.getLevel().toString());
        assertEquals("AUD_NT_ARR", loggingEvent5.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent5.getFormattedMessage().startsWith("[AUD_NT_ARR] BEFORE"));
        assertTrue(loggingEvent5.getFormattedMessage().endsWith(" - Test3"));

        final ILoggingEvent loggingEvent6 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent6.getMarker().getName());
        assertEquals("ERROR", loggingEvent6.getLevel().toString());
        assertEquals("AUD_NT_ARR", loggingEvent6.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent6.getFormattedMessage().startsWith("[AUD_NT_ARR] FAILURE"));
        assertTrue(loggingEvent6.getFormattedMessage().endsWith(" - ERROR in calling method pippo"));
    }
}
