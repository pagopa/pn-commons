package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ResultFilter;
import reactor.core.publisher.Mono;

/**
 * Classe da estendere, che implementerà la logica effettiva del filtro
 * La logica si baserà sugli argomenti.
 * <p>
 * Nel caso in cui il nextHandler sia presente, se la valutazione del filtro è positiva
 * l'implementazione normalmente dovrà ritornare il result del nextHandler, anche se è
 * libera di ritornare un risultato (positivo o negativo) interrompendo quindi la catena di filtri.
 *
 * @param <T> L'istanza dell'oggetto di valutazione
 * @param <C> Eventuale contesto da utilizzare nella valutazione
 * @param <R> Risultato della valutazione
 */
public abstract class Handler<T, C, R extends ResultFilter> {

    protected Handler<T, C, R> nextHandler;

    /**
     * Metodo per valutare la logica di filtro
     *
     * @param item oggetto su cui valutare il filtro
     * @param ruleContext eventuale contesto da utilizzare nella valutazione
     * @return risultato della valutazione.
     */
    abstract Mono<R> filter(T item, C ruleContext);

    /**
     * Imposta l'eventuale prossimo step nella catena di filtri.
     * @param nextHandler istanza del prossimo filtro da invocare nella catena.
     */
    void setNext(Handler<T, C, R> nextHandler){
        this.nextHandler = nextHandler;
    }
}
