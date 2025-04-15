package it.pagopa.pn.commons.log.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DimensionEmf extends Dimension {

    public DimensionEmf(Dimension dimension) { super(dimension.getName(), dimension.getValue()); }

    @Override
    public String toJson() {
        return String.format("\"%s\"", getName());
    }
}
