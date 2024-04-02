package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListChainResultFilter;
import it.pagopa.pn.commons.rules.model.ResultFilter;
import it.pagopa.pn.commons.rules.model.RuleModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class ListRuleEngineHandlerExampleItem extends ListRuleEngineHandler<List<RuleModel>, ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> {
    public ListRuleEngineHandlerExampleItem(ListChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> parent) {
        super(parent);
    }

    @Override
    protected Handler resolveHandlerFromRule(RuleModel r) {
        return r.getRuleType().equals("regola1")?getHandler(true):getHandler2(false);
    }



    @NotNull
    private static Handler<ExampleItem, Object, ListChainResultFilter<ExampleItem>> getHandler(boolean result) {
        return new Handler<>() {
            @Override
            Mono<ListChainResultFilter<ExampleItem>> filter(ExampleItem item, Object ruleContext) {

                if (!result)
                    return Mono.just(new ListChainResultFilter<ExampleItem>(item, result)).doOnNext(r -> log.info("handler1 item={} returned={}", r.getItem(), r.isResult()));
                else {
                    if (nextHandler != null)
                        return nextHandler.filter(item, ruleContext);
                    else
                        return Mono.just(new ListChainResultFilter<ExampleItem>(item, result)).doOnNext(r -> log.info("handler1 item={} returned={}", r.getItem(), r.isResult()));
                }
            }
        };
    }


    @NotNull
    private static Handler<ExampleItem, Object, ListChainResultFilter<ExampleItem>> getHandler2(boolean result) {
        return new Handler<>() {
            @Override
            Mono<ListChainResultFilter<ExampleItem>> filter(ExampleItem item, Object ruleContext) {

                if (!result)
                    return Mono.just(new ListChainResultFilter<ExampleItem>(item, result)).doOnNext(r -> log.info("handler2 item={} returned={}", r.getItem(), r.isResult()));
                else {
                    if (nextHandler != null)
                        return nextHandler.filter(item, ruleContext);
                    else
                        return Mono.just(new ListChainResultFilter<ExampleItem>(item, result)).doOnNext(r -> log.info("handler2 item={} returned={}", r.getItem(), r.isResult()));
                }
            }
        };
    }

}
