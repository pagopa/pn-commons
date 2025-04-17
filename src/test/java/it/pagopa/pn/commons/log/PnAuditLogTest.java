package it.pagopa.pn.commons.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import it.pagopa.pn.commons.log.dto.metrics.Dimension;
import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.log.dto.metrics.Metric;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.pagopa.pn.commons.log.PnAuditLog.AUDIT_TYPE;
import static it.pagopa.pn.commons.log.PnAuditLogEventType.AUD_ACC_LOGIN;
import static it.pagopa.pn.commons.log.PnAuditLogEventType.AUD_NT_AAR;
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
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_AAR, "Test1").iun("CNZS-RZBB-HJAT-202205-E-1").build();

        // call method under test
        logEvent.log();
        //---- Call to business method
        logEvent.generateResult(PnAuditLogType.FAILURE, "ERROR in calling method").log();
        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent1 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent1.getMarker().getName());
        assertEquals("INFO", loggingEvent1.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent1.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent1.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertTrue(loggingEvent1.getFormattedMessage().endsWith(" - Test1"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1", logEvent.getMdc().get("iun"));


        final ILoggingEvent loggingEvent2 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent2.getMarker().getName());
        assertEquals("ERROR", loggingEvent2.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent2.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent2.getFormattedMessage().startsWith("[AUD_NT_AAR] FAILURE"));
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
        assertEquals("AUDIT10Y", loggingEvent3.getMarker().getName());
        assertEquals("INFO", loggingEvent3.getLevel().toString());
        assertEquals("AUD_ACC_LOGIN", loggingEvent3.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent3.getFormattedMessage().startsWith("[AUD_ACC_LOGIN] BEFORE"));
        assertTrue(loggingEvent3.getFormattedMessage().endsWith(" - Test format 1 = pippo"));
        assertEquals("CUSTOM_VALUE",logEvent.getMdc().get("CUSTOM_MDC"));

        final ILoggingEvent loggingEvent4 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent4.getMarker().getName());
        assertEquals("INFO", loggingEvent4.getLevel().toString());
        assertEquals("AUD_ACC_LOGIN", loggingEvent4.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent4.getFormattedMessage().startsWith("[AUD_ACC_LOGIN] SUCCESS"));
        assertEquals("CUSTOM_VALUE",logEvent.getMdc().get("CUSTOM_MDC"));
    }

    @Test
    void testAuditLog3() {
        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_AAR, "Test3")
                .build()
                // call method under test
                .log();
        //---- Call to business method
        logEvent.generateFailure("ERROR in calling method {}", "pippo").log();

        List<ILoggingEvent> logsList = listAppender.list;

        final ILoggingEvent loggingEvent5 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent5.getMarker().getName());
        assertEquals("INFO", loggingEvent5.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent5.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent5.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertTrue(loggingEvent5.getFormattedMessage().endsWith(" - Test3"));

        final ILoggingEvent loggingEvent6 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent6.getMarker().getName());
        assertEquals("ERROR", loggingEvent6.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent6.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent6.getFormattedMessage().startsWith("[AUD_NT_AAR] FAILURE"));
        assertTrue(loggingEvent6.getFormattedMessage().endsWith(" - ERROR in calling method pippo"));
    }

    @Test
    void testAuditLogWarning() {
        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_AAR, "Test1").iun("CNZS-RZBB-HJAT-202205-E-1").build();

        // call method under test
        logEvent.log();
        final String message = "ERROR in calling method";
        logEvent.generateWarning(message).log();

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent1 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent1.getMarker().getName());
        assertEquals("INFO", loggingEvent1.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent1.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent1.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertTrue(loggingEvent1.getFormattedMessage().endsWith(" - Test1"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1", logEvent.getMdc().get("iun"));


        final ILoggingEvent loggingEvent2 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent2.getMarker().getName());
        assertEquals("WARN", loggingEvent2.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent2.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent2.getFormattedMessage().startsWith("[AUD_NT_AAR] WARNING"));
        assertTrue(loggingEvent2.getFormattedMessage().endsWith(message));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1",logEvent.getMdc().get("iun"));
    }

    @Test
    void testAuditLogPnfMetricInAfterMethod() {
        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_AAR, "Test1").iun("CNZS-RZBB-HJAT-202205-E-1").build();
        logEvent.setMetricFormatType(PnAuditLogMetricFormatType.PNF.name());

        List<GeneralMetric> metricsArray = List.of(getGeneralMetric("1"),getGeneralMetric("2"));

        // call method under test
        logEvent.log();
        final String message = "ERROR in calling method";
        logEvent.generateWarning(message, metricsArray).log();

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent1 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent1.getMarker().getName());
        assertEquals("INFO", loggingEvent1.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent1.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent1.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertTrue(loggingEvent1.getFormattedMessage().endsWith(" - Test1"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1", logEvent.getMdc().get("iun"));


        final ILoggingEvent loggingEvent2 = logsList.get(1);
        assertEquals("LS_APPEND_RAW", loggingEvent2.getMarker().getName());
        assertEquals("INFO", loggingEvent2.getLevel().toString());
        assertTrue(loggingEvent2.getMessage().equals("Metrics log"));
        assertNotNull(loggingEvent2.getMarker());
        assertEquals("PNApplicationMetrics=[{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[{\"name\":\"Key1_1\",\"value\":\"Value1\"},{\"name\":\"Key2_1\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric1_1\",\"Value\":100},{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[{\"name\":\"Key1_1\",\"value\":\"Value1\"},{\"name\":\"Key2_1\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric2_1\",\"Value\":200},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[{\"name\":\"Key1_2\",\"value\":\"Value1\"},{\"name\":\"Key2_2\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric1_2\",\"Value\":100},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[{\"name\":\"Key1_2\",\"value\":\"Value1\"},{\"name\":\"Key2_2\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric2_2\",\"Value\":200}]", loggingEvent2.getMarker().toString());
    }

    @Test
    void testAuditLogPnfMetricInBeforeMethod() {
        List<GeneralMetric> metricsArray = List.of(getGeneralMetric("1"),getGeneralMetric("2"));

        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before( AUD_NT_AAR, "Test1", metricsArray).iun("CNZS-RZBB-HJAT-202205-E-1").build();
        logEvent.setMetricFormatType(PnAuditLogMetricFormatType.PNF.name());



        // call method under test
        logEvent.log();
        final String message = "ERROR in calling method";
        logEvent.generateWarning(message).log();

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent = logsList.get(0);
        assertEquals("LS_APPEND_RAW", loggingEvent.getMarker().getName());
        assertEquals("INFO", loggingEvent.getLevel().toString());
        assertTrue(loggingEvent.getMessage().equals("Metrics log"));
        assertNotNull(loggingEvent.getMarker());
        assertEquals("PNApplicationMetrics=[{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[{\"name\":\"Key1_1\",\"value\":\"Value1\"},{\"name\":\"Key2_1\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric1_1\",\"Value\":100},{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[{\"name\":\"Key1_1\",\"value\":\"Value1\"},{\"name\":\"Key2_1\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric2_1\",\"Value\":200},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[{\"name\":\"Key1_2\",\"value\":\"Value1\"},{\"name\":\"Key2_2\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric1_2\",\"Value\":100},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[{\"name\":\"Key1_2\",\"value\":\"Value1\"},{\"name\":\"Key2_2\",\"value\":\"Value2\"}],\"Timestamp\":1744818188,\"Name\":\"Metric2_2\",\"Value\":200}]", loggingEvent.getMarker().toString());


        final ILoggingEvent loggingEvent2 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent2.getMarker().getName());
        assertEquals("INFO", loggingEvent2.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent2.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent2.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1",logEvent.getMdc().get("iun"));
    }

    @Test
    void testAuditLogEmfMetricInAfterMethod() {
        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before(AUD_NT_AAR, "Test1").iun("CNZS-RZBB-HJAT-202205-E-1").build();
        logEvent.setMetricFormatType(PnAuditLogMetricFormatType.EMF.name());

        List<GeneralMetric> metricsArray = List.of(getGeneralMetric("1"), getGeneralMetric("2"));

        // call method under test
        logEvent.log();
        final String message = "ERROR in calling method";
        logEvent.generateWarning(message, metricsArray).log();

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent1 = logsList.get(0);
        assertEquals("AUDIT10Y", loggingEvent1.getMarker().getName());
        assertEquals("INFO", loggingEvent1.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent1.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent1.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertTrue(loggingEvent1.getFormattedMessage().endsWith(" - Test1"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1", logEvent.getMdc().get("iun"));


        final ILoggingEvent loggingEvent2 = logsList.get(1);
        assertEquals("LS_MAP_FIELDS", loggingEvent2.getMarker().getName());
        assertEquals("INFO", loggingEvent2.getLevel().toString());
        assertTrue(loggingEvent2.getMessage().equals("Metrics log"));
        assertNotNull(loggingEvent2.getMarker());
        ArrayList<Marker> markers = new ArrayList<>();
        Iterator<Marker> iterator = loggingEvent2.getMarker().iterator();
        while(iterator.hasNext()) {
            markers.add(iterator.next());
        }
        assertEquals("_aws={\"Timestamp\": " + metricsArray.get(0).getTimestamp() + ", \"CloudWatchMetrics\": [{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[[\"Key1_1\",\"Key2_1\"]],\"Metrics\":[{\"Name\":\"Metric1_1\"},{\"Name\":\"Metric2_1\"}]},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[[\"Key1_2\",\"Key2_2\"]],\"Metrics\":[{\"Name\":\"Metric1_2\"},{\"Name\":\"Metric2_2\"}]}]}", markers.get(0).toString());

    }

    @Test
    void testAuditLogEmfMetricInBeforeMethod() {
        List<GeneralMetric> metricsArray = List.of(getGeneralMetric("1"), getGeneralMetric("2"));

        // create AuditEvents
        PnAuditLogEvent logEvent = auditLogger.before(AUD_NT_AAR, "Test1", metricsArray).iun("CNZS-RZBB-HJAT-202205-E-1").build();
        logEvent.setMetricFormatType(PnAuditLogMetricFormatType.EMF.name());

        // call method under test
        logEvent.log();
        final String message = "ERROR in calling method";
        logEvent.generateWarning(message).log();

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        final ILoggingEvent loggingEvent = logsList.get(0);
        assertEquals("LS_MAP_FIELDS", loggingEvent.getMarker().getName());
        assertEquals("INFO", loggingEvent.getLevel().toString());
        assertTrue(loggingEvent.getMessage().equals("Metrics log"));
        assertNotNull(loggingEvent.getMarker());
        ArrayList<Marker> markers = new ArrayList<>();
        Iterator<Marker> iterator = loggingEvent.getMarker().iterator();
        while(iterator.hasNext()) {
            markers.add(iterator.next());
        }
        assertEquals("_aws={\"Timestamp\": " + metricsArray.get(0).getTimestamp() + ", \"CloudWatchMetrics\": [{\"Namespace\":\"MultiNamespace_1\",\"Dimensions\":[[\"Key1_1\",\"Key2_1\"]],\"Metrics\":[{\"Name\":\"Metric1_1\"},{\"Name\":\"Metric2_1\"}]},{\"Namespace\":\"MultiNamespace_2\",\"Dimensions\":[[\"Key1_2\",\"Key2_2\"]],\"Metrics\":[{\"Name\":\"Metric1_2\"},{\"Name\":\"Metric2_2\"}]}]}", markers.get(0).toString());


        final ILoggingEvent loggingEvent1 = logsList.get(1);
        assertEquals("AUDIT10Y", loggingEvent1.getMarker().getName());
        assertEquals("INFO", loggingEvent1.getLevel().toString());
        assertEquals("AUD_NT_AAR", loggingEvent1.getMDCPropertyMap().get(AUDIT_TYPE));
        assertTrue(loggingEvent1.getFormattedMessage().startsWith("[AUD_NT_AAR] BEFORE"));
        assertTrue(loggingEvent1.getFormattedMessage().endsWith(" - Test1"));
        assertEquals("CNZS-RZBB-HJAT-202205-E-1", logEvent.getMdc().get("iun"));
    }

    @NotNull
    private static GeneralMetric getGeneralMetric(String i) {
        GeneralMetric generalMetric = new GeneralMetric();
        generalMetric.setNamespace("MultiNamespace_" +i);
        generalMetric.setTimestamp(1744818188);

        Dimension dimension1 = new Dimension("Key1_" +i, "Value1");
        Dimension dimension2 = new Dimension("Key2_" +i, "Value2");
        generalMetric.setDimensions(List.of(dimension1, dimension2));

        Metric metric1 = new Metric("Metric1_" +i, 100);
        Metric metric2 = new Metric("Metric2_" +i, 200);
        generalMetric.setMetrics(List.of(metric1, metric2));
        return generalMetric;
    }
}
