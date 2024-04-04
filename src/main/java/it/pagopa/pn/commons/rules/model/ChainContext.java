package it.pagopa.pn.commons.rules.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Contesto base per un filtro con una lista di oggetti
 * Prevede la presenza nel contesto dell'intera lista degli oggetti da valutare
 * e la lista degli attuali risultati valutati
 *
 * @param <C> oggetto da valutare
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChainContext<C extends Serializable> implements Serializable {

    private C context;

}
