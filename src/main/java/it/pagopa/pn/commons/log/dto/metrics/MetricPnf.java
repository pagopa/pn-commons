package it.pagopa.pn.commons.log.dto.metrics;

public class MetricPnf extends Metric {

    MetricPnf (Metric metric) {
        super(metric.getName(), metric.getValue());
    }

    @Override
    public String toJson() {
        return String.format("\"Name\":\"%s\",\"Value\":%s", getName(), getValue());
    }

}
