package it.pagopa.pn.commons.log.dto.metrics;

public class PnfMetric extends GeneralMetric {

    public PnfMetric(GeneralMetric metric) {
        super(metric.getNamespace(), metric.getDimensions(), metric.getTimestamp(), metric.getMetrics(), metric.getUnit());
    }

    @Override
    public String toJson() {
        return getMetrics().stream().map(metric -> {
            String dimensionsString = getDimensions().stream().map(Dimension::toJsonPnf)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            String metricsString = metric.toJsonPnf();

            return String.format("{\"Namespace\":\"%s\",\"Dimensions\":[%s],\"Timestamp\":\"%s\",%s,\"Unit\":\"%s\"}",
                    getNamespace(), dimensionsString, getTimestamp(), metricsString,getUnit());
        }).reduce((a, b) -> a + "," + b).orElse("");
    }

}
