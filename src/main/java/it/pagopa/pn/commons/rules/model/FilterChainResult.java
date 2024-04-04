package it.pagopa.pn.commons.rules.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
/**
 * Risultato del filtro
 */
public class FilterChainResult implements Serializable {

    private boolean success;
    private String code;
    private String diagnostic;
}
