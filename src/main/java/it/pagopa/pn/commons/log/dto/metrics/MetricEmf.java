package it.pagopa.pn.commons.log.dto.metrics;

public class MetricEmf extends Metric {

    public MetricEmf(Metric metric) { super(metric.getName(), metric.getValue()); }


    public String toJson(String unit) {
        return String.format("{\"Name\":\"%s\",\"Unit\":\"%s\"}", getName(), unit);
    }
}
