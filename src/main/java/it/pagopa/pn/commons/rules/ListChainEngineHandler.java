package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.FilterChainResult;
import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListFilterChainResult;
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
 * @param <T> vedi ChainHandler
 * @param <C> vedi ChainHandler
 */
@Component
public class ListChainEngineHandler<T extends Serializable, C extends Serializable> {

    public Flux<ListFilterChainResult<T>> filterItems(C context, List<T> items, ListChainHandler<T, C> handler){
        // il concatMap concatena i mono sequenzialmente 1 alla volta, che è il desiderata,
        // dato che ogni mono riceve in input il risultato aggiornato degli item precedenti
        ListChainContext<T, C> chainContext = new ListChainContext<>(context, items);
        return Flux.fromIterable(items)
                .concatMap(item -> setupAndExecuteFilter(chainContext, handler, item));
    }

    @NotNull
    private Mono<ListFilterChainResult<T>> setupAndExecuteFilter(ListChainContext<T, C> context, ListChainHandler<T, C> handler, T item) {
        ListChainContext<T, C> deepCopyContext = SerializationUtils.clone(context);
        return handler.doFilter(item, deepCopyContext)
                .map(r -> postProcessFilterResult(context, item, r));
    }

    private ListFilterChainResult<T> postProcessFilterResult(ListChainContext<T, C> context, T item, FilterChainResult r) {
        ListFilterChainResult<T> finalResult = new ListFilterChainResult<>();
        finalResult.setSuccess(r.isSuccess());
        finalResult.setItem(item);

        context.getActualResults().add(finalResult);
        return finalResult;
    }
}
