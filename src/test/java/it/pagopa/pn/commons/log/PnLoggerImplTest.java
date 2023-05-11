package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PnLoggerImplTest {

    PnLogger fooLogger;
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void beforeEach() {
        // get Logback Logger
        fooLogger = PnLogger.getLogger(PnLoggerImplTest.class.getName());

        // create and start a ListAppender
        listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        ((ch.qos.logback.classic.Logger)fooLogger.getSlf4jLogger()).addAppender(listAppender);
    }

    @Test
    void infoStartingProcess() {
        //Given
        String str = "processo";


        //When
        fooLogger.infoStartingProcess(str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Starting process " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoEndingProcess() {
        //Given
        String str = "processo";


        //When
        fooLogger.infoEndingProcess(str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Ending process " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoChecking() {
        //Given
        String str = "processo";


        //When
        fooLogger.infoChecking(str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Checking " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoInvokingExternalService() {
        //Given
        String str = "processo";
        String service = PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_CHANNELS;


        //When
        fooLogger.infoInvokingExternalService(service, str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Invoking external service " + service + " " + str + ". Waiting Sync response.", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoInvokingAsyncExternalService() {
        //Given
        String str = "processo";
        String service = PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_CHANNELS;
        String correlationid = "1234";


        //When
        fooLogger.infoInvokingAsyncExternalService(service, str, correlationid);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Invoking external service " + service + " " + str + ". " + correlationid + " for Async response.", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());

    }


    @Test
    void isFatalEnabled() {
        assertTrue(fooLogger.isFatalEnabled());
        assertTrue(fooLogger.isFatalEnabled(null));
    }


    @Test
    void testFatal1() {
        //Given
        String str = "errore grave";


        //When
        fooLogger.fatal(str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("ALLARM!: " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("ALLARM!", logsList.get(0)
                .getMarker().getName());
    }

    @Test
    void testFatal2() {
        //Given
        String str = "errore grave p1={} p2={}";


        //When
        fooLogger.fatal(str, 0, "pippo");

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("ALLARM!: " + "errore grave p1=0 p2=pippo", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("ALLARM!", logsList.get(0)
                .getMarker().getName());
    }

    @Test
    void testFatal3() {
        //Given
        String str = "errore grave p1={} p2={}";

        //When
        fooLogger.fatal(str, 0, "pippo", new RuntimeException("errore"));

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("ALLARM!: " + "errore grave p1=0 p2=pippo", logsList.get(0)
                .getFormattedMessage());
        assertTrue(logsList.get(0)
                .getThrowableProxy().getClassName().contains("java.lang.RuntimeException"));
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("ALLARM!", logsList.get(0)
                .getMarker().getName());
    }
}