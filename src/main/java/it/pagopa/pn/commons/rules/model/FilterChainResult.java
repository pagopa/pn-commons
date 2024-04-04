package it.pagopa.pn.commons.rules.model;

import lombok.*;

import java.io.Serializable;

/**
 * Risultato del filtro
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FilterChainResult implements Serializable {

    private boolean success;
    private String code;
    private String diagnostic;
}
