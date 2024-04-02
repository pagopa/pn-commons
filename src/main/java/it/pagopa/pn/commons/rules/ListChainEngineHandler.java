package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListChainResultFilter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

/**
 * Semplice implementazione di un valutatore di filtri su una lista di oggetti.
 * Di fatto, wrappa la chiamata verso l'handler per singolo oggetto, e arricchisce il contesto
 * con i risultati man mano che gli oggetti vengono valutati
 * Il contesto viene clonato prima di ogni step, per evitare che step successivi possano alterare i
 * risultati precedenti.
 *
 * @param <T> vedi Handler
 * @param <C> vedi Handler
 * @param <R> vedi Handler
 */

@AllArgsConstructor
@Component
public class ListChainEngineHandler<T extends Serializable, C extends ListChainContext<T>, R extends ListChainResultFilter<T>> {

    private SimpleChainEngineHandler<T, C, R> simpleChainEngineHandler;

    public Flux<R> filterItems(C context, List<T> items, Handler<T, C, R> handler){
        // il concatMap concatena i mono sequenzialmente 1 alla volta, che è il desiderata,
        // dato che ogni mono riceve in input il risultato aggiornato degli item precedenti
        return Flux.fromIterable(items)
                .concatMap(item -> setupAndExecuteFilter(context, handler, item));
    }

    @NotNull
    private Mono<R> setupAndExecuteFilter(C context, Handler<T, C, R> handler, T item) {
        C deepCopyContext = SerializationUtils.clone(context);
        return simpleChainEngineHandler
                .filterItem(deepCopyContext, item, handler)
                .map(r -> postProcessFilterResult(context, r));
    }

    private R postProcessFilterResult(C context, R r) {
        context.addResult(r);
        return r;
    }
}
