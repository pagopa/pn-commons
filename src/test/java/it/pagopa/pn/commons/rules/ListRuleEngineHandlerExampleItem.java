package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class ListRuleEngineHandlerExampleItem extends ListRuleEngineHandler<List<RuleModel>, ExampleItem, ListChainContext<ExampleItem>> {
    public ListRuleEngineHandlerExampleItem(ListChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>> parent) {
        super(parent);
    }

    @Override
    protected Handler resolveHandlerFromRule(RuleModel r) {
        return r.getRuleType().equals("regola1")?getHandler(true):getHandler2(false);
    }



    @NotNull
    private static Handler<ExampleItem, Object> getHandler(boolean result) {
        return new Handler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, Object ruleContext) {

                if (!result)
                    return Mono.just(FilterHandlerResult.FAIL);
                else {
                    return Mono.just(FilterHandlerResult.NEXT);
                }
            }
        };
    }


    @NotNull
    private static Handler<ExampleItem, Object> getHandler2(boolean result) {
        return new Handler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, Object ruleContext) {

                if (!result)
                    return Mono.just(FilterHandlerResult.FAIL);
                else {
                    return Mono.just(FilterHandlerResult.SUCCESS);
                }
            }
        };
    }

}
