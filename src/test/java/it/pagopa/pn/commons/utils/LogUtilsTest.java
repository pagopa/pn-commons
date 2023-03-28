package it.pagopa.pn.commons.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class LogUtilsTest {

    @ParameterizedTest
    @CsvSource(value ={ "email@email.it, e***l@email.it", "em@email.it, ***@email.it", "NULL, null" }, nullValues={"NULL"})
    void maskEmailAddress(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskEmailAddress(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }


    @ParameterizedTest
    @CsvSource(value ={ "333123456, 3*****456", "NULL, null" }, nullValues={"NULL"})
    void maskNumber(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskNumber(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }



    @ParameterizedTest
    @CsvSource(value ={ "una qualche stringa lunga, u*********************nga", "un, ***", "NULL, null" }, nullValues={"NULL"})
    void maskGeneric(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskGeneric(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }



    @ParameterizedTest
    @CsvSource(value ={ "CSRGGL44L13H501E, C************01E", "NULL, null" }, nullValues={"NULL"})
    void maskTaxId(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskTaxId(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }


    @Test
    void maskString() {
        //Given
        String str = "una qualche stringa lunga";

        //When
        String result = LogUtils.maskString(str, 1, 5, '^');

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals("u^^^^ualche stringa lunga", result);
    }

    @Test
    void getMessageWithSafeUrl() {
        //Given
        String fileName = "filename.pdf";
        String url = "https://fakeurlfordownload?token=faketoken";

        //When
        String messageResult = LogUtils.createAuditLogMessageForDownloadDocument( fileName, url, null );

        //Then
        Assertions.assertNotNull( messageResult );
        Assertions.assertEquals( "filename=filename.pdf, url=https://fakeurlfordownload", messageResult );
    }

    @Test
    void getMessageWithRetryAfter() {
        //Given
        String fileName = "filename.pdf";
        String retryAfter = "3600";

        //When
        String messageResult = LogUtils.createAuditLogMessageForDownloadDocument( fileName, null, retryAfter );

        //Then
        Assertions.assertNotNull( messageResult );
        Assertions.assertEquals( "filename=filename.pdf, retryAfter=3600", messageResult );
    }


    @Test
    void alarm_noparams() {
        //Given
        String str = "errore grave";
        // get Logback Logger
        Logger fooLogger = LoggerFactory.getLogger(LogUtilsTest.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        ((ch.qos.logback.classic.Logger)fooLogger).addAppender(listAppender);


        //When
        LogUtils.alarm(log, str);

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
    void alarm_someparams() {
        //Given
        String str = "errore grave p1={} p2={}";
        // get Logback Logger
        Logger fooLogger = LoggerFactory.getLogger(LogUtilsTest.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        ((ch.qos.logback.classic.Logger)fooLogger).addAppender(listAppender);


        //When
        LogUtils.alarm(log, str, 0, "pippo");

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
    void alarm_someparams_exc() {
        //Given
        String str = "errore grave p1={} p2={}";
        // get Logback Logger
        Logger fooLogger = LoggerFactory.getLogger(LogUtilsTest.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        ((ch.qos.logback.classic.Logger)fooLogger).addAppender(listAppender);


        //When
        LogUtils.alarm(log, str, 0, "pippo", new RuntimeException("errore"));

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
