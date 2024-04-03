package it.pagopa.pn.commons.rules.model;

import java.io.Serializable;
import java.util.List;

/**
 * Contesto base per un filtro con una lista di oggetti
 * Prevede la presenza nel contesto dell'intera lista degli oggetti da valutare
 * e la lista degli attuali risultati valutati
 *
 * @param <T> oggetto da valutare
 */
public interface ListChainContext<T extends Serializable> extends Serializable {

    /**
     * Recupera gli item del contesto
     *
     * @return lista degli item
     */
    List<T> getItems();

    /**
     * Recupera i risultati presenti
     *
     * @return lista dei risultati
     */
    List<ListFilterChainResult<T>> getActualResults();

    /**
     * Permette di aggiungere un esito alla lista
     *
     * @param result il risultato da aggiungere
     */
    void addResult(ListFilterChainResult<T> result);

}
