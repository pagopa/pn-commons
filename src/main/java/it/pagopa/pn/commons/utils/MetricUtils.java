package it.pagopa.pn.commons.utils;

import ch.qos.logback.classic.Logger;
import it.pagopa.pn.commons.log.PnAuditLogMetricFormatType;
import it.pagopa.pn.commons.log.dto.metrics.EmfMetric;
import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.log.dto.metrics.PnfMetric;
import org.slf4j.Marker;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;
import static net.logstash.logback.marker.Markers.appendRaw;

public class MetricUtils {

    private MetricUtils() {}

    public static void generateMetricsLog(Logger logger, List<GeneralMetric> metricsArray, String message, String metricFormatType) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return;
        }
        if(PnAuditLogMetricFormatType.PNF.name().equals(metricFormatType)) {
            addPNFMetric(metricsArray, logger, message);
        } else if (PnAuditLogMetricFormatType.EMF.name().equals(metricFormatType)) {
            addEMFMetric(metricsArray, logger, message);
        }
    }

    private static void addEMFMetric(List<GeneralMetric> metricsArray, Logger logger, String message) {
        logger.info(appendEntries(MetricUtils.generateJsonEMFMetricParameters(metricsArray)).and(appendRaw("_aws", MetricUtils.generateJsonEMFMetric(metricsArray))), message);
    }

    private static void addPNFMetric(List<GeneralMetric> metricsArray, Logger logger, String message) {
        logger.info(appendRaw("PNApplicationMetrics", MetricUtils.generateJsonPNFMetric(metricsArray)), message);
    }

    public static String generateJsonPNFMetric(List<GeneralMetric> metricsArray) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return "";
        }
        String metricsObject = metricsArray.stream()
                .map(metric -> new PnfMetric(metric).toJson())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        return String.format("[%s]", metricsObject);
    }

    public static Marker generateMetricsMarker(List<GeneralMetric> metricsArray, String metricFormatType) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return null;
        }

        if(PnAuditLogMetricFormatType.PNF.name().equals(metricFormatType)) {
            return appendRaw("PNApplicationMetrics", MetricUtils.generateJsonPNFMetric(metricsArray));
        } else if (PnAuditLogMetricFormatType.EMF.name().equals(metricFormatType)) {
            return appendEntries(MetricUtils.generateJsonEMFMetricParameters(metricsArray)).and(appendRaw("_aws", MetricUtils.generateJsonEMFMetric(metricsArray)));
        }

        return null;
    }

    public static String generateJsonEMFMetric(List<GeneralMetric> metricsArray) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return "";
        }
        String metricsObject = metricsArray.stream()
                .map(metric -> new EmfMetric(metric).toJson())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        return String.format("{\"Timestamp\": %s, \"CloudWatchMetrics\": [%s]}", metricsArray.get(0).getTimestamp(), metricsObject);
    }

    public static Map<String, Object> generateJsonEMFMetricParameters(List<GeneralMetric> metricsArray) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return new HashMap<>();
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        metricsArray.forEach(
                generalMetric -> {
                    if (!CollectionUtils.isEmpty(generalMetric.getDimensions())) {
                        generalMetric.getDimensions().forEach(
                                dimension -> {
                                    String dimensionName = dimension.getName();
                                    String dimensionValue = dimension.getValue();
                                    resultMap.put(dimensionName, dimensionValue);
                                }
                        );
                    }
                    if (!CollectionUtils.isEmpty(generalMetric.getMetrics())) {
                        generalMetric.getMetrics().forEach(
                                metric -> {
                                    String metricName = metric.getName();
                                    long metricValue = metric.getValue();
                                    resultMap.put(metricName, metricValue);
                                }
                        );
                    }
                }
        );
        return resultMap;
    }
}
