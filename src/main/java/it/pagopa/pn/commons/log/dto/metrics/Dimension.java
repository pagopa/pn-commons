package it.pagopa.pn.commons.log.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimension {
    private String name;
    private String value;

    public String toJsonPnf() {
        return String.format("{\"name\":\"%s\",\"value\":\"%s\"}", name, value);
    }

    public String toJsonEmf() {
        return String.format("\"%s\"", name);
    }
}
