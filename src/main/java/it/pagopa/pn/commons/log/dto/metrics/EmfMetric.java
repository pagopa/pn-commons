package it.pagopa.pn.commons.log.dto.metrics;

import org.springframework.util.CollectionUtils;

public class EmfMetric extends GeneralMetric {

    public EmfMetric(GeneralMetric metric) {
        super(metric.getNamespace(), metric.getDimensions(), metric.getTimestamp(), metric.getMetrics(), metric.getUnit());
    }

    @Override
    public String toJson() {
        String dimensionsString = getDimensions().stream().map(Dimension::toJsonEmf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        String metricsString = CollectionUtils.isEmpty(getMetrics()) ? "[]" :
                getMetrics().stream()
                        .map(metric -> metric.toJsonEmf(getUnit()))
                        .reduce((a, b) -> a + "," + b)
                        .orElse("");

        return String.format("{\"Namespace\":\"%s\",\"Dimensions\":[[%s]],\"Metrics\":[%s]}",
                getNamespace(), dimensionsString, metricsString);
    }

}
