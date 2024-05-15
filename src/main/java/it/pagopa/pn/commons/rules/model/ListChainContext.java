package it.pagopa.pn.commons.rules.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contesto base per un filtro con una lista di oggetti
 * Prevede la presenza nel contesto dell'intera lista degli oggetti da valutare
 * e la lista degli attuali risultati valutati
 *
 * @param <T> oggetto da valutare
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ListChainContext<T extends Serializable, C extends Serializable> extends ChainContext<C> implements Serializable {

    private List<T> items;
    private final List<ListFilterChainResult<T>> actualResults = new ArrayList<>();

    public ListChainContext(C context, List<T> items) {
        super(context);
        this.items = items;
    }
}
