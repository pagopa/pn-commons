package it.pagopa.pn.commons.log.dto.metrics;

public class PnfMetric extends GeneralMetric {

    public PnfMetric(GeneralMetric metric) {
        super(metric.getNamespace(), metric.getDimensions(), metric.getTimestamp(), metric.getMetrics(), metric.getUnit());
    }

    @Override
    public String toJson() {
        return getMetrics().stream().map(metric -> {
            String dimensionsString = getDimensions().stream().map(dimension -> new DimensionPnf(dimension).toJson())
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            String metricsString = new MetricPnf(metric).toJson();

            return String.format("{\"Namespace\":\"%s\",\"Dimensions\":[%s],\"Timestamp\":\"%s\",%s}",
                    getNamespace(), dimensionsString, getTimestamp(), metricsString);
        }).reduce((a, b) -> a + "," + b).orElse("");
    }

}
