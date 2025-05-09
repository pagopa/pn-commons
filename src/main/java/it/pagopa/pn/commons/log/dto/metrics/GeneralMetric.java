package it.pagopa.pn.commons.log.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralMetric {
    private String namespace;
    private List<Dimension> dimensions;
    private long timestamp;
    private List<Metric> metrics;
    private String unit;

    public String toJson() {
        return StringUtils.EMPTY;
    }
}
