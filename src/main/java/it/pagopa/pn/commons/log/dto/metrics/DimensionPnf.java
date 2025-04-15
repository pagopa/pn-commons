package it.pagopa.pn.commons.log.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DimensionPnf extends Dimension {

    DimensionPnf(Dimension dimension) {
        super(dimension.getName(), dimension.getValue());
    }

    @Override
    public String toJson() {
        return String.format("{\"name\":\"%s\",\"value\":\"%s\"}", getName(), getValue());
    }
}
