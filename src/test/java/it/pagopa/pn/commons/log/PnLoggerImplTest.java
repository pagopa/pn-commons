package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import it.pagopa.pn.commons.log.dto.metrics.Dimension;
import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.log.dto.metrics.Metric;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        fooLogger.logStartingProcess(str);

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
        fooLogger.logEndingProcess(str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Ending process " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }


    @Test
    void infoEndingProcessFail() {
        //Given
        String str = "processo";


        //When
        fooLogger.logEndingProcess(str, false, "test");

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Ending process " + str + " with errors=test", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.WARN, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoChecking() {
        //Given
        String str = "processo";


        //When
        fooLogger.logChecking(str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Checking " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }


    @Test
    void infoCheckingOutcome() {
        //Given
        String str = "processo";


        //When
        fooLogger.logCheckingOutcome(str, true);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Checking " + str + " passed", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }



    @Test
    void infoCheckingOutcomeFail() {
        //Given
        String str = "processo";


        //When
        fooLogger.logCheckingOutcome(str, false, "some error");

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Checking " + str + " failed reason=some error", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.WARN, logsList.get(0)
                .getLevel());
    }



    @Test
    void infoCheckingOutcomeFail2() {
        //Given
        String str = "processo";


        //When
        fooLogger.logCheckingOutcome(str, false, null);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Checking " + str + " failed reason=<not specified>", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.WARN, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoInvokingExternalService() {
        //Given
        String str = "processo";
        String service = PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_CHANNELS;


        //When
        fooLogger.logInvokingExternalService(service, str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Invoking external service " + service + " " + str + ". Waiting Sync response.", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void infoInvokingDownstreamExternalServiceD() {
        //Given
        String str = "processo";
        String service = PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_CHANNELS;


        //When
        fooLogger.logInvokingExternalDownstreamService(service, str);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("[DOWNSTREAM] Invoking external service " + service + " " + str + ". Waiting Sync response.", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());
    }

    @Test
    void logEndingDownstreamProcessFail() {
        //Given
        String str = "processo";

        //When
        fooLogger.logInvokationResultDownstreamFailed(str, null);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("[DOWNSTREAM] Service " + str + " returned errors=<not specified>", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
    }

    @Test
    void logEndingDownstreamProcessNotFound() {
        //Given
        String str = "processo";

        //When
        fooLogger.logInvokationResultDownstreamNotFound(str, null);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("[DOWNSTREAM] Service " + str + " returned errors=<not specified>", logsList.get(0)
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
        fooLogger.logInvokingAsyncExternalService(service, str, correlationid);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("Invoking external service " + service + " " + str + ". " + correlationid + " for Async response.", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0)
                .getLevel());

    }

    @Test
    void infoInvokingDownstreamAsyncExternalService() {
        //Given
        String str = "processo";
        String service = PnLogger.EXTERNAL_SERVICES.PN_EXTERNAL_CHANNELS;
        String correlationid = "1234";


        //When
        fooLogger.logInvokingAsyncExternalDownstreamService(service, str, correlationid);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("[DOWNSTREAM] Invoking external service " + service + " " + str + ". " + correlationid + " for Async response.", logsList.get(0)
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
        Assertions.assertEquals("* FATAL * ALLARM!: " + str, logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("* FATAL * ALLARM!", logsList.get(0)
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
        Assertions.assertEquals("* FATAL * ALLARM!: " + "errore grave p1=0 p2=pippo", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("* FATAL * ALLARM!", logsList.get(0)
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
        Assertions.assertEquals("* FATAL * ALLARM!: " + "errore grave p1=0 p2=pippo", logsList.get(0)
                .getFormattedMessage());
        assertTrue(logsList.get(0)
                .getThrowableProxy().getClassName().contains("java.lang.RuntimeException"));
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("* FATAL * ALLARM!", logsList.get(0)
                .getMarker().getName());
    }

    @Data
    @AllArgsConstructor
    private class TestObj {
        private String pippo;
    }

    @Test
    void testFatal4() {
        //Given
        TestObj t = new TestObj("pippo");
        t.setPippo("pippo");
        String str = "errore grave p1={}";



        //When
        fooLogger.fatal(str, t);

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("* FATAL * ALLARM!: " + "errore grave p1=PnLoggerImplTest.TestObj(pippo=pippo)", logsList.get(0)
                .getFormattedMessage());
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("* FATAL * ALLARM!", logsList.get(0)
                .getMarker().getName());
    }


    @Test
    void testFatal5() {
        //Given
        String str = "errore grave ";

        //When
        fooLogger.fatal(str,  new RuntimeException("errore"));

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals("* FATAL * ALLARM!: " + "errore grave ", logsList.get(0)
                .getFormattedMessage());
        assertTrue(logsList.get(0)
                .getThrowableProxy().getClassName().contains("java.lang.RuntimeException"));
        Assertions.assertEquals(Level.ERROR, logsList.get(0)
                .getLevel());
        Assertions.assertEquals("* FATAL * ALLARM!", logsList.get(0)
                .getMarker().getName());
    }

    @Test
    void testTrace(){
        String str = "trace qualcosa";
        assertLog(Level.TRACE, str, () -> fooLogger.trace(str), false, null);
        assertLog(Level.TRACE, str+"p1=pippo", () -> fooLogger.trace(str+"p1={}", "pippo"), false, null);
        assertLog(Level.TRACE, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.trace(str+"p1={}", new TestObj("pippo")), false, null);
        assertLog(Level.TRACE, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.trace(str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, null);
        assertLog(Level.TRACE, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.trace(str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, null);
        assertLog(Level.TRACE, str, () -> fooLogger.trace(str, new RuntimeException("error")), true, null);

        Marker m = MarkerFactory.getMarker("test");

        assertLog(Level.TRACE, str, () -> fooLogger.trace(m, str), false, m);
        assertLog(Level.TRACE, str+"p1=pippo", () -> fooLogger.trace(m, str+"p1={}", "pippo"), false, m);
        assertLog(Level.TRACE, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.trace(m, str+"p1={}", new TestObj("pippo")), false, m);
        assertLog(Level.TRACE, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.trace(m, str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, m);
        assertLog(Level.TRACE, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.trace(m, str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, m);
        assertLog(Level.TRACE, str, () -> fooLogger.trace(m, str, new RuntimeException("error")), true, m);
    }

    @Test
    void testDebug() {
        String str = "debug qualcosa";
        assertLog(Level.DEBUG, str, () -> fooLogger.debug(str), false, null);
        assertLog(Level.DEBUG, str+"p1=pippo", () -> fooLogger.debug(str+"p1={}", "pippo"), false, null);
        assertLog(Level.DEBUG, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.debug(str+"p1={}", new TestObj("pippo")), false, null);
        assertLog(Level.DEBUG, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.debug(str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, null);
        assertLog(Level.DEBUG, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.debug(str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, null);
        assertLog(Level.DEBUG, str, () -> fooLogger.debug(str, new RuntimeException("error")), true, null);

        Marker m = MarkerFactory.getMarker("test");

        assertLog(Level.DEBUG, str, () -> fooLogger.debug(m, str), false, m);
        assertLog(Level.DEBUG, str+"p1=pippo", () -> fooLogger.debug(m, str+"p1={}", "pippo"), false, m);
        assertLog(Level.DEBUG, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.debug(m, str+"p1={}", new TestObj("pippo")), false, m);
        assertLog(Level.DEBUG, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.debug(m, str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, m);
        assertLog(Level.DEBUG, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.debug(m, str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, m);
        assertLog(Level.DEBUG, str, () -> fooLogger.debug(m, str, new RuntimeException("error")), true, m);
    }


    @Test
    void testInfo() {
        String str = "info qualcosa";
        assertLog(Level.INFO, str, () -> fooLogger.info(str), false, null);
        assertLog(Level.INFO, str+"p1=pippo", () -> fooLogger.info(str+"p1={}", "pippo"), false, null);
        assertLog(Level.INFO, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.info(str+"p1={}", new TestObj("pippo")), false, null);
        assertLog(Level.INFO, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.info(str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, null);
        assertLog(Level.INFO, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.info(str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, null);
        assertLog(Level.INFO, str, () -> fooLogger.info(str, new RuntimeException("error")), true, null);

        Marker m = MarkerFactory.getMarker("test");

        assertLog(Level.INFO, str, () -> fooLogger.info(m, str), false, m);
        assertLog(Level.INFO, str+"p1=pippo", () -> fooLogger.info(m, str+"p1={}", "pippo"), false, m);
        assertLog(Level.INFO, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.info(m, str+"p1={}", new TestObj("pippo")), false, m);
        assertLog(Level.INFO, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.info(m, str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, m);
        assertLog(Level.INFO, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.info(m, str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, m);
        assertLog(Level.INFO, str, () -> fooLogger.info(m, str, new RuntimeException("error")), true, m);
    }



    @Test
    void testWarn() {
        String str = "warn qualcosa";
        assertLog(Level.WARN, str, () -> fooLogger.warn(str), false, null);
        assertLog(Level.WARN, str+"p1=pippo", () -> fooLogger.warn(str+"p1={}", "pippo"), false, null);
        assertLog(Level.WARN, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.warn(str+"p1={}", new TestObj("pippo")), false, null);
        assertLog(Level.WARN, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.warn(str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, null);
        assertLog(Level.WARN, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.warn(str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, null);
        assertLog(Level.WARN, str, () -> fooLogger.warn(str, new RuntimeException("error")), true, null);

        Marker m = MarkerFactory.getMarker("test");

        assertLog(Level.WARN, str, () -> fooLogger.warn(m, str), false, m);
        assertLog(Level.WARN, str+"p1=pippo", () -> fooLogger.warn(m, str+"p1={}", "pippo"), false, m);
        assertLog(Level.WARN, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.warn(m, str+"p1={}", new TestObj("pippo")), false, m);
        assertLog(Level.WARN, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.warn(m, str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, m);
        assertLog(Level.WARN, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.warn(m, str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, m);
        assertLog(Level.WARN, str, () -> fooLogger.warn(m, str, new RuntimeException("error")), true, m);
    }

    @Test
    void testError() {
        String str = "error qualcosa";
        assertLog(Level.ERROR, str, () -> fooLogger.error(str), false, null);
        assertLog(Level.ERROR, str+"p1=pippo", () -> fooLogger.error(str+"p1={}", "pippo"), false, null);
        assertLog(Level.ERROR, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.error(str+"p1={}", new TestObj("pippo")), false, null);
        assertLog(Level.ERROR, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.error(str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, null);
        assertLog(Level.ERROR, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.error(str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, null);
        assertLog(Level.ERROR, str, () -> fooLogger.error(str, new RuntimeException("error")), true, null);

        Marker m = MarkerFactory.getMarker("test");

        assertLog(Level.ERROR, str, () -> fooLogger.error(m, str), false, m);
        assertLog(Level.ERROR, str+"p1=pippo", () -> fooLogger.error(m, str+"p1={}", "pippo"), false, m);
        assertLog(Level.ERROR, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo)", () -> fooLogger.error(m, str+"p1={}", new TestObj("pippo")), false, m);
        assertLog(Level.ERROR, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto", () -> fooLogger.error(m, str+"p1={} p2={}", new TestObj("pippo"), "pluto"), false, m);
        assertLog(Level.ERROR, str+"p1=PnLoggerImplTest.TestObj(pippo=pippo) p2=pluto p3=paperino", () -> fooLogger.error(m, str+"p1={} p2={} p3={}", new TestObj("pippo"), "pluto", "paperino"), false, m);
        assertLog(Level.ERROR, str, () -> fooLogger.error(m, str, new RuntimeException("error")), true, m);
    }

    @Test
    void testMetrichePNF() {
        //Given
        List<GeneralMetric> metricsArray = List.of(getGeneralMetric("1"),getGeneralMetric("2"));
        String str = "{\"PNApplicationMetrics\":[{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[{\"name\":\"Key1_1\",\"value\":\"Value1\"},{\"name\":\"Key2_1\",\"value\":\"Value2\"}],\"Timestamp\":\"2023-10-05T12:00:00Z\",\"Name\":\"Metric1_1\",\"Value\":\"100\",\"Unit\":\"Milliseconds\"},{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[{\"name\":\"Key1_1\",\"value\":\"Value1\"},{\"name\":\"Key2_1\",\"value\":\"Value2\"}],\"Timestamp\":\"2023-10-05T12:00:00Z\",\"Name\":\"Metric2_1\",\"Value\":\"200\",\"Unit\":\"Milliseconds\"},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[{\"name\":\"Key1_2\",\"value\":\"Value1\"},{\"name\":\"Key2_2\",\"value\":\"Value2\"}],\"Timestamp\":\"2023-10-05T12:00:00Z\",\"Name\":\"Metric1_2\",\"Value\":\"100\",\"Unit\":\"Milliseconds\"},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[{\"name\":\"Key1_2\",\"value\":\"Value1\"},{\"name\":\"Key2_2\",\"value\":\"Value2\"}],\"Timestamp\":\"2023-10-05T12:00:00Z\",\"Name\":\"Metric2_2\",\"Value\":\"200\",\"Unit\":\"Milliseconds\"}]}";

        //When
        fooLogger.logMetric(metricsArray, PnAuditLogMetricFormatType.PNF.name());

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals(str, logsList.get(0).getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0).getLevel());
    }

    @Test
    void testMetricheEMF() {
        //Given
        List<GeneralMetric> metricsArray = List.of(getGeneralMetric("1"),getGeneralMetric("2"));
        String str = "{\"_aws\":{\"Timestamp\": \"2023-10-05T12:00:00Z\", \"CloudWatchMetrics\": [{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[[\"Key1_1\",\"Key2_1\"]],\"Metrics\":[{\"Name\":\"Metric1_1\",\"Unit\":\"Milliseconds\"},{\"Name\":\"Metric2_1\",\"Unit\":\"Milliseconds\"}]},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[[\"Key1_2\",\"Key2_2\"]],\"Metrics\":[{\"Name\":\"Metric1_2\",\"Unit\":\"Milliseconds\"},{\"Name\":\"Metric2_2\",\"Unit\":\"Milliseconds\"}]}]},\"Key2_2\":\"Value2\",\"Key1_1\":\"Value1\",\"Key2_1\":\"Value2\",\"Key1_2\":\"Value1\",\"Metric2_1\":\"200\",\"Metric1_2\":\"100\",\"Metric2_2\":\"200\",\"Metric1_1\":\"100\"}";

        //When
        fooLogger.logMetric(metricsArray, PnAuditLogMetricFormatType.EMF.name());

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals(str, logsList.get(0).getFormattedMessage());
        Assertions.assertEquals(Level.INFO, logsList.get(0).getLevel());
    }

    @NotNull
    private static GeneralMetric getGeneralMetric(String i) {
        GeneralMetric generalMetric = new GeneralMetric();
        generalMetric.setNamespace("MultiNamespace_" +i);
        generalMetric.setTimestamp("2023-10-05T12:00:00Z");
        generalMetric.setUnit("Milliseconds");

        Dimension dimension1 = new Dimension("Key1_" +i, "Value1");
        Dimension dimension2 = new Dimension("Key2_" +i, "Value2");
        generalMetric.setDimensions(List.of(dimension1, dimension2));

        Metric metric1 = new Metric("Metric1_" +i, "100");
        Metric metric2 = new Metric("Metric2_" +i, "200");
        generalMetric.setMetrics(List.of(metric1, metric2));
        return generalMetric;
    }

    void assertLog(Level level, String result, Runnable r, boolean checkException, Marker marker) {
        //Given

        //When
        listAppender.list = new ArrayList<>();
        r.run();

        //Then
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertEquals(result, logsList.get(0).getFormattedMessage());
        Assertions.assertEquals(level, logsList.get(0).getLevel());
        if (checkException)
            assertTrue(logsList.get(0).getThrowableProxy().getClassName().contains("java.lang.RuntimeException"));

        if (marker != null)
            Assertions.assertEquals(marker.getName(), logsList.get(0).getMarker().getName());
    }

}