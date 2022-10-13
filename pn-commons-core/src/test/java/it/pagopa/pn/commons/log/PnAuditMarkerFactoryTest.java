package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PnAuditMarkerFactoryTest {

    @Test
    void testMarker() {
        Logger logger = (Logger) LoggerFactory.getLogger(PnAuditMarkerFactory.class);
        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        logger.addAppender(listAppender);

        // call method under test
        logger.info(PnAuditMarkerFactory.get5yMarker(), "Test 5 year marker");
        logger.info(PnAuditMarkerFactory.get10yMarker(), "Test 10 year marker");
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("AUDIT5Y", logsList.get(0).getMarker().getName());
        assertEquals("AUDIT10Y", logsList.get(1).getMarker().getName());
    }
}
