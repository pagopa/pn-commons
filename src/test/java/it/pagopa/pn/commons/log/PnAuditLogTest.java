package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PnAuditLogTest {

    @Test
    void testMarker() {

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        PnAuditLog.logger.addAppender(listAppender);

        // create AuditEvents
        PnAuditLogEvent event1 = new PnAuditLogEvent(PnAuditLogEventType.AUD_NT_ARR, "Test1");
        PnAuditLogEvent event2 = new PnAuditLogEvent(PnAuditLogEventType.AUD_ACC_LOGIN, "Test format {} = {}", "1", "pippo");

        // call method under test
        PnAuditLog.logBefore(event1);
        PnAuditLog.logAfter(event1, false);

        PnAuditLog.logBefore(event2);
        PnAuditLog.logAfter(event2, true);

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("AUDIT10Y", logsList.get(0).getMarker().getName());
        assertEquals("INFO", logsList.get(0).getLevel().toString());
        assertEquals("[AUD_NT_ARR] - Before - Test1", logsList.get(0).getFormattedMessage());
        assertEquals("AUDIT10Y", logsList.get(1).getMarker().getName());
        assertEquals("ERROR", logsList.get(1).getLevel().toString());
        assertEquals("[AUD_NT_ARR] - After - Test1", logsList.get(1).getFormattedMessage());
        assertEquals("AUDIT5Y", logsList.get(2).getMarker().getName());
        assertEquals("INFO", logsList.get(2).getLevel().toString());
        assertEquals("[AUD_ACC_LOGIN] - Before - Test format 1 = pippo", logsList.get(2).getFormattedMessage());
        assertEquals("AUDIT5Y", logsList.get(3).getMarker().getName());
        assertEquals("INFO", logsList.get(3).getLevel().toString());
        assertEquals("[AUD_ACC_LOGIN] - After - Test format 1 = pippo", logsList.get(3).getFormattedMessage());
    }
}
