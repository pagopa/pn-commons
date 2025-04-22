package it.pagopa.pn.commons.log.dto.metrics;

public class MetricEmf extends Metric {

    public MetricEmf(Metric metric) { super(metric.getName(), metric.getValue()); }

    @Override
    public String toJson() {
        return String.format("{\"Name\":\"%s\"}", getName());
    }
}
