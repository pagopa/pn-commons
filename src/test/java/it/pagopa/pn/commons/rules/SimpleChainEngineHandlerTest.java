package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.rules.model.ResultFilter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

class SimpleChainEngineHandlerTest {


    SimpleChainEngineHandler<ExampleItem, Object, ResultFilter> simpleChainEngineHandler;
    @BeforeEach
    void setup() {
        simpleChainEngineHandler = new SimpleChainEngineHandler<>();

    }

    @Test
    void filterItemTrue() {
        Handler<ExampleItem, Object, ResultFilter> h = getHandler(true);

        ResultFilter r = simpleChainEngineHandler.filterItem(new Object(), new ExampleItem(), h).block();

        Assertions.assertTrue(r.isResult());
    }


    @Test
    void filterItemFalse() {
        Handler<ExampleItem, Object, ResultFilter> h = getHandler(false);

        ResultFilter r = simpleChainEngineHandler.filterItem(new Object(), new ExampleItem(), h).block();

        Assertions.assertFalse(r.isResult());
    }


    @Test
    void filterItemChainFalse() {

        Handler<ExampleItem, Object, ResultFilter> h = getHandler(true);
        Handler<ExampleItem, Object, ResultFilter> h1 = getHandler(false);
        h.setNext(h1);



        ResultFilter r = simpleChainEngineHandler.filterItem(new Object(), new ExampleItem(), h).block();

        Assertions.assertFalse(r.isResult());
    }


    @Test
    void filterItemChainFalse2() {

        Handler<ExampleItem, Object, ResultFilter> h = getHandler(false);
        Handler<ExampleItem, Object, ResultFilter> h1 = getHandler(true);
        h.setNext(h1);



        ResultFilter r = simpleChainEngineHandler.filterItem(new Object(), new ExampleItem(), h).block();

        Assertions.assertFalse(r.isResult());
    }




    @Test
    void filterItem_Exception() {
        Handler<ExampleItem, Object, ResultFilter> h = new Handler<>() {
            @Override
            Mono<ResultFilter> filter(ExampleItem item, Object ruleContext) {

                return Mono.error(new PnInternalException("errore", "errore!"));
            }
        };

        Mono<ResultFilter> mono = simpleChainEngineHandler.filterItem(new Object(), new ExampleItem(), h);

        Assertions.assertThrows(PnInternalException.class, mono::block);
    }


    @NotNull
    private static Handler<ExampleItem, Object, ResultFilter> getHandler(boolean result) {
        return new Handler<>() {
            @Override
            Mono<ResultFilter> filter(ExampleItem item, Object ruleContext) {

                if (!result)
                    return Mono.just(new ResultFilter(result));
                else {
                    if (nextHandler != null)
                        return nextHandler.filter(item, ruleContext);
                    else
                        return Mono.just(new ResultFilter(result));
                }
            }
        };
    }

}