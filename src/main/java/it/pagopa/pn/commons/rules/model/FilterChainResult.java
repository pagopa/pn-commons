package it.pagopa.pn.commons.rules.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
/**
 * Risultato del filtro, può essere esteso per allegare informazioni
 */
public class FilterChainResult implements Serializable {

    private boolean success;
}
