package it.pagopa.pn.commons.log.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Metric {
    private String name;
    private String value;

    @Override
    public String toString() {
        return String.format("{\"name\":\"%s\",\"value\":\"%s\"}", name, value);
    }
}
