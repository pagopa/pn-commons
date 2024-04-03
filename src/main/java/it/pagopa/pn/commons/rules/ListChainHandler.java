package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ChainContext;
import it.pagopa.pn.commons.rules.model.FilterChainResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import it.pagopa.pn.commons.rules.model.ListChainContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.Serializable;

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
 */
@Slf4j
public abstract class ListChainHandler<T extends Serializable, C extends Serializable> extends BaseChainHandler<T, C> {

    /**
     * Metodo per valutare la logica di filtro
     *
     * @param item oggetto su cui valutare il filtro
     * @param context eventuale contesto da utilizzare nella valutazione
     * @return risultato della valutazione.
     */
    public abstract Mono<FilterHandlerResult> filter(T item, ListChainContext<T, C> context);


    protected final Mono<FilterChainResult> doFilter(T item, ChainContext<C> ruleContext){
        return filter(item, (ListChainContext<T, C>)ruleContext)
                .doOnNext(r -> log.debug("filter result={}", r))
                .flatMap(handlerResult -> super.manageHandlerResult(item, ruleContext, handlerResult));
    }
}
