package it.pagopa.pn.commons.log.dto.metrics;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmfMetricTest {

    @Test
    void toJsonHandlesMultipleDimensionsAndMetrics() {
        GeneralMetric generalMetric = getGeneralMetric();

        EmfMetric emfMetric = new EmfMetric(generalMetric);

        String expectedJson = "{\"Namespace\":\"MultiNamespace\",\"Dimensions\":[[\"Key1\",\"Key2\"]],\"Metrics\":[{\"Name\":\"Metric1\"},{\"Name\":\"Metric2\"}]}";
        assertEquals(expectedJson, emfMetric.toJson());
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