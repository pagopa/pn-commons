package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.rules.model.FilterChainResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResultEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class SimpleChainEngineHandlerTest {


    SimpleChainEngineHandler<ExampleItem, ExampleContext> simpleChainEngineHandler;
    @BeforeEach
    void setup() {
        simpleChainEngineHandler = new SimpleChainEngineHandler<>();

    }

    @Test
    void filterItemTrue() {
        ChainHandler<ExampleItem, ExampleContext> h = getHandler(true);

        FilterChainResult r = simpleChainEngineHandler.filterItem(new ExampleContext(), new ExampleItem(), h).block();

        Assertions.assertTrue(r.isSuccess());
    }


    @Test
    void filterItemFalse() {
        ChainHandler<ExampleItem, ExampleContext> h = getHandler(false);

        FilterChainResult r = simpleChainEngineHandler.filterItem(new ExampleContext(), new ExampleItem(), h).block();

        Assertions.assertFalse(r.isSuccess());
    }


    @Test
    void filterItemChainFalse() {

        ChainHandler<ExampleItem, ExampleContext> h = getHandler(true);
        ChainHandler<ExampleItem, ExampleContext> h1 = getHandler(false);
        h.setNext(h1);



        FilterChainResult r = simpleChainEngineHandler.filterItem(new ExampleContext(), new ExampleItem(), h).block();

        Assertions.assertFalse(r.isSuccess());
    }

    @Test
    void filterItemChainTrue() {

        ChainHandler<ExampleItem, ExampleContext> h = getSuccessHandler();
        ChainHandler<ExampleItem, ExampleContext> h1 = getHandler(false);
        h.setNext(h1);



        FilterChainResult r = simpleChainEngineHandler.filterItem(new ExampleContext(), new ExampleItem(), h).block();

        Assertions.assertTrue(r.isSuccess());
    }


    @Test
    void filterItemChainFalse2() {

        ChainHandler<ExampleItem, ExampleContext> h = getHandler(false);
        ChainHandler<ExampleItem, ExampleContext> h1 = getHandler(true);
        h.setNext(h1);



        FilterChainResult r = simpleChainEngineHandler.filterItem(new ExampleContext(), new ExampleItem(), h).block();

        Assertions.assertFalse(r.isSuccess());
    }




    @Test
    void filterItem_Exception() {
        ChainHandler<ExampleItem, ExampleContext> h = new ChainHandler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ExampleContext ruleContext) {

                return Mono.error(new PnInternalException("errore", "errore!"));
            }
        };

        Mono<FilterChainResult> mono = simpleChainEngineHandler.filterItem(new ExampleContext(), new ExampleItem(), h);

        Assertions.assertThrows(PnInternalException.class, mono::block);
    }


    @NotNull
    private static ChainHandler<ExampleItem, ExampleContext> getHandler(boolean result) {
        return new ChainHandler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ExampleContext ruleContext) {

                if (!result)
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.FAIL, null,null));
                else {
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.NEXT, null,null));
                }
            }
        };
    }

    @NotNull
    private static ChainHandler<ExampleItem, ExampleContext> getSuccessHandler() {
        return new ChainHandler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ExampleContext ruleContext) {

                return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, null,null));

            }
        };
    }

}