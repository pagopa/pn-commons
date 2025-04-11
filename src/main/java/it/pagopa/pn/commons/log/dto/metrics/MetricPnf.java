package it.pagopa.pn.commons.log.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MetricPnf extends Metric {

    MetricPnf (Metric metric) {
        super(metric.getName(), metric.getValue());
    }

    @Override
    public String toJson() {
        return String.format("\"Name\":\"%s\",\"Value\":\"%s\"", getName(), getValue());
    }

}
