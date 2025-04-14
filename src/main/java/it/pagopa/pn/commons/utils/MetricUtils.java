package it.pagopa.pn.commons.utils;

import it.pagopa.pn.commons.log.dto.metrics.EmfMetric;
import it.pagopa.pn.commons.log.dto.metrics.GeneralMetric;
import it.pagopa.pn.commons.log.dto.metrics.PnfMetric;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricUtils {

    private MetricUtils() {}
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

    public static String generateJsonEMFMetric(List<GeneralMetric> metricsArray) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return "";
        }
        String metricsObject = metricsArray.stream()
                .map(metric -> new EmfMetric(metric).toJson())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        return String.format("{\"Timestamp\": \"%s\", \"CloudWatchMetrics\": [%s]}", metricsArray.get(0).getTimestamp(), metricsObject);
    }

    public static Map<String, String> generateJsonEMFMetricParameters(List<GeneralMetric> metricsArray) {
        if (CollectionUtils.isEmpty(metricsArray)) {
            return new HashMap<>();
        }
        HashMap<String, String> resultMap = new HashMap<>();
        metricsArray.forEach(
                generalMetric -> {
                    generalMetric.getDimensions().forEach(
                            dimension -> {
                                String dimensionName = dimension.getName();
                                String dimensionValue = dimension.getValue();
                                resultMap.put(dimensionName, dimensionValue);
                            }
                    );
                    generalMetric.getMetrics().forEach(
                            metric -> {
                                String metricName = metric.getName();
                                String metricValue = metric.getValue();
                                resultMap.put(metricName, metricValue);
                            }
                    );
                }
        );
        return resultMap;
    }
}
