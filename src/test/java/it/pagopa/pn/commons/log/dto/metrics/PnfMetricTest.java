package it.pagopa.pn.commons.log.dto.metrics;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PnfMetricTest {

    @Test
    void toJsonHandlesMultipleDimensionsAndMetrics() {
        GeneralMetric generalMetric = getGeneralMetric();

        PnfMetric pnfMetric = new PnfMetric(generalMetric);

        String expectedJson = "{\"Namespace\":\"MultiNamespace\",\"Dimensions\":[{\"name\":\"Key1\",\"value\":\"Value1\"},{\"name\":\"Key2\",\"value\":\"Value2\"}],\"Name\":\"Metric1\",\"Value\":\"100\",\"Unit\":\"Milliseconds\"},{\"Namespace\":\"MultiNamespace\",\"Dimensions\":[{\"name\":\"Key1\",\"value\":\"Value1\"},{\"name\":\"Key2\",\"value\":\"Value2\"}],\"Name\":\"Metric2\",\"Value\":\"200\",\"Unit\":\"Milliseconds\"}";
        assertEquals(expectedJson, pnfMetric.toJson());
    }

    @NotNull
    private static GeneralMetric getGeneralMetric() {
        GeneralMetric generalMetric = new GeneralMetric();
        generalMetric.setNamespace("MultiNamespace");
        generalMetric.setTimestamp("2023-10-05T12:00:00Z");
        generalMetric.setUnit("Milliseconds");

        Dimension dimension1 = new Dimension("Key1", "Value1");
        Dimension dimension2 = new Dimension("Key2", "Value2");
        generalMetric.setDimensions(List.of(dimension1, dimension2));

        Metric metric1 = new Metric("Metric1", "100");
        Metric metric2 = new Metric("Metric2", "200");
        generalMetric.setMetrics(List.of(metric1, metric2));
        return generalMetric;
    }

}