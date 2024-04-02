package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ResultFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Semplice implementazione di un valutatore di filtri.
 * Di fatto, wrappa la chiamata verso l'handler
 *
 * @param <T> vedi Handler
 * @param <C> vedi Handler
 * @param <R> vedi Handler
 */
@Component
@Slf4j
public class SimpleChainEngineHandler<T, C, R extends ResultFilter> {
    public Mono<R> filterItem(C context, T item, Handler<T, C, R> handler){
        // richiama wrappando il filtro, tipo con log
        log.debug("invoking filter item handler={} item={} context={}", handler.toString(), item, context);
        return handler.filter(item, context)
                .doOnNext(r -> log.info("invoked filter handler={}  item={} context={} result={}", handler, item, context, r));
    }
}
