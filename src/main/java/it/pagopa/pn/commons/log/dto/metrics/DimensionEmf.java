package it.pagopa.pn.commons.log.dto.metrics;

public class DimensionEmf extends Dimension {

    public DimensionEmf(Dimension dimension) { super(dimension.getName(), dimension.getValue()); }

    @Override
    public String toJson() {
        return String.format("\"%s\"", getName());
    }
}
