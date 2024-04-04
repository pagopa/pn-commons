package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListFilterChainResult;
import it.pagopa.pn.commons.rules.model.RuleModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;

/**
 * Implementazione di un valutatore di regole.
 * Traduce in una lista di ChainHandler un set di "regole"
 *
 * @param <U>
 * @param <T>
 * @param <C>
 */
@AllArgsConstructor
@Slf4j
public abstract class ListRuleEngineHandler<U extends List<? extends RuleModel>, T extends Serializable, C extends Serializable> {

    private ListChainEngineHandler<T, C> listChainEngineHandler;
    /**
     * Metodo da implementare con il meccanismo di risoluzione di un handler in base alla regola
     * @param r regola da risolvere
     * @return istanza (thread-safe) dell'handler.
     */
    protected abstract ListChainHandler<T, C> resolveHandlerFromRule(RuleModel r);


    public Flux<ListFilterChainResult<T>> filterItems(C context, List<T> items, U rules){
        // risolve la catena di handlers, utilizzando le regole passate
        ListChainHandler<T, C> firstHandlerOfChain = resolveHandlersFromRules(rules);
        return listChainEngineHandler.filterItems(context, items, firstHandlerOfChain);
    }

    private ListChainHandler<T,C> resolveHandlersFromRules(U rules) {
        log.debug("resolving rules rules={}", rules);
        ListChainHandler<T,C> lastHandlerResolved = null;

        // ciclo al contrario, perchè alla fine mi interessa ritornare il primo elemento della catena
        for(int i = rules.size()-1;i>=0;i--)
        {
            // Risolve l'handler, e poi imposta aggiorna il nextHandler
            // con il lastHandlerResolved della catena.
            // si noti che l'ultimo handler avrà null come next, interrompendo la catena
            ListChainHandler<T,C> nextHandler = resolveHandlerFromRule(rules.get(i));
            nextHandler.setNext(lastHandlerResolved);

            lastHandlerResolved = nextHandler;
        }

        return lastHandlerResolved;
    }
}
