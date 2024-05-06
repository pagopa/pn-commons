package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class ListRuleEngineHandlerExampleItem extends ListRuleEngineHandler<RuleModel, ExampleItem, ExampleContext> {
    public ListRuleEngineHandlerExampleItem(ListChainEngineHandler<ExampleItem, ExampleContext> parent) {
        super(parent);
    }

    @Override
    protected ListChainHandler resolveHandlerFromRule(RuleModel r) {
        return r.getRuleType().equals("regola1")?getHandler(true):getHandler2(false);
    }



    @NotNull
    private static ListChainHandler<ExampleItem, ExampleContext> getHandler(boolean result) {
        return new ListChainHandler<>() {

            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem, ExampleContext> ruleContext) {

                if (!result)
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.FAIL, "COD1","diag1"));
                else {
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.NEXT, "COD2","diag2"));
                }
            }
        };
    }


    @NotNull
    private static ListChainHandler<ExampleItem, ExampleContext> getHandler2(boolean result) {
        return new ListChainHandler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem, ExampleContext> ruleContext) {

                if (!result)
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.FAIL, null,null));
                else {
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.NEXT, null,null));
                }
            }
        };
    }

}
