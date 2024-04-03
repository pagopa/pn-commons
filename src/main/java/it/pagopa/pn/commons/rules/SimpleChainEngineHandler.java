package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ChainContext;
import it.pagopa.pn.commons.rules.model.FilterChainResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Semplice implementazione di un valutatore di filtri.
 * Di fatto, wrappa la chiamata verso l'handler
 *
 * @param <T> vedi ChainHandler
 * @param <C> vedi ChainHandler
 */
@Component
@Slf4j
public class SimpleChainEngineHandler<T, C extends Serializable> {
    public Mono<FilterChainResult> filterItem(C context, T item, ChainHandler<T, C> handler){
        // richiama wrappando il filtro, tipo con log
        log.debug("invoking filter item handler={} item={} context={}", handler.toString(), item, context);
        ChainContext<C> chainContext = new ChainContext<>(context);
        return handler.doFilter(item, chainContext)
                .doOnNext(r -> log.info("invoked filter handler={}  item={} context={} result={}", handler, item, context, r));
    }
}
