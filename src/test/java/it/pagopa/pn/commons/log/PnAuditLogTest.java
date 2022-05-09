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

        // call method under test
        PnAuditLogEvent event1 = new PnAuditLogEvent(PnAuditLogEventType.AUD_NT_ARR, "Test1");
        PnAuditLogEvent event2 = new PnAuditLogEvent(PnAuditLogEventType.AUD_ACC_LOGIN, "Test format {}", "1");
        PnAuditLog.info(event1);
        PnAuditLog.info(event2);
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("AUDIT10Y", logsList.get(0).getMarker().getName());
        assertEquals("AUDIT5Y", logsList.get(1).getMarker().getName());
    }
}
