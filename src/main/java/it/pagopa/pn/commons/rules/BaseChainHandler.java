package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ChainContext;
import it.pagopa.pn.commons.rules.model.FilterChainResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
abstract class BaseChainHandler<T, C extends Serializable> {
    protected BaseChainHandler<T, C > nextHandler;


    /**
     * Imposta l'eventuale prossimo step nella catena di filtri.
     * @param nextHandler istanza del prossimo filtro da invocare nella catena.
     */
    void setNext(BaseChainHandler<T, C> nextHandler){
        this.nextHandler = nextHandler;
    }

    protected  abstract Mono<FilterChainResult> doFilter(T item, ChainContext<C> ruleContext);

    @NotNull Mono<FilterChainResult> manageHandlerResult(T item, ChainContext<C> ruleContext, FilterHandlerResult handlerResult) {
        return switch (handlerResult.getResult()) {
            case SUCCESS -> Mono.just(new FilterChainResult(true, handlerResult.getCode(), handlerResult.getDiagnostic() ));
            case FAIL -> Mono.just(new FilterChainResult(false, handlerResult.getCode(), handlerResult.getDiagnostic()));
            case NEXT -> {
                if (this.nextHandler != null) {
                    log.debug("there is a nextHandler, returning that result");
                    yield this.nextHandler.doFilter(item, ruleContext);
                }
                else {
                    yield Mono.just(new FilterChainResult(true, handlerResult.getCode(), handlerResult.getDiagnostic()));
                }
            }
        };
    }
}
