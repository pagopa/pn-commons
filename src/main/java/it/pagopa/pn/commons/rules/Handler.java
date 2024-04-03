package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.FilterChainResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
 */
@Slf4j
public abstract class Handler<T, C> {
    protected Handler<T, C> nextHandler;

    /**
     * Metodo per valutare la logica di filtro
     *
     * @param item oggetto su cui valutare il filtro
     * @param ruleContext eventuale contesto da utilizzare nella valutazione
     * @return risultato della valutazione.
     */
    public abstract Mono<FilterHandlerResult> filter(T item, C ruleContext);

    /**
     * Imposta l'eventuale prossimo step nella catena di filtri.
     * @param nextHandler istanza del prossimo filtro da invocare nella catena.
     */
    void setNext(Handler<T, C> nextHandler){
        this.nextHandler = nextHandler;
    }

    protected final Mono<FilterChainResult> doFilter(T item, C ruleContext){
        return filter(item, ruleContext)
                .doOnNext(r -> log.debug("filter result={}", r))
                .flatMap(handlerResult -> manageHandlerResult(item, ruleContext, handlerResult));
    }

    @NotNull
    private Mono<FilterChainResult> manageHandlerResult(T item, C ruleContext, FilterHandlerResult handlerResult) {
        return switch (handlerResult) {
            case SUCCESS -> Mono.just(new FilterChainResult(true));
            case FAIL -> Mono.just(new FilterChainResult(false));
            case NEXT -> {
                if (this.nextHandler != null) {
                    log.debug("there is a nextHandler, returning that result");
                    yield this.nextHandler.doFilter(item, ruleContext);
                }
                else {
                    yield Mono.just(new FilterChainResult(true));
                }
            }
        };
    }
}
