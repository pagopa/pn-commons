package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResultEnum;
import it.pagopa.pn.commons.rules.model.ListChainContext;
import reactor.core.publisher.Mono;

import java.io.Serializable;

class EmptyRuleSuccesPlaceholderListChainHandler<T extends Serializable, C extends Serializable> extends ListChainHandler<T, C> {
    @Override
    public Mono<FilterHandlerResult> filter(T item, ListChainContext<T, C> context) {
        return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, "EMPTY_RULE", "No rule set"));
    }
}
