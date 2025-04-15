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

    public String toJsonPnf() {
        return String.format("\"Name\":\"%s\",\"Value\":\"%s\"", name, value);
    }

    public String toJsonEmf(String unit) {
        return String.format("{\"Name\":\"%s\",\"Unit\":\"%s\"}", name, unit);
    }
}
