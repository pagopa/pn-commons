package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListFilterChainResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

class ListChainEngineHandlerTest {


    ListChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>> listChainEngineHandler;
    @BeforeEach
    void setup() {
        SimpleChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>> simpleChainEngineHandler = new SimpleChainEngineHandler<>();
        listChainEngineHandler = new ListChainEngineHandler<>(simpleChainEngineHandler);

    }
    @Test
    void filterItemsTrue() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>> h = getHandler();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertTrue(r.get(1).isResult());
        Assertions.assertTrue(r.get(2).isResult());
    }

    @Test
    void filterItemsOneElement() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>> h = getHandler();

        List<ExampleItem> items = List.of(new ExampleItem("info1"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());

    }


    @Test
    void filterItemsWithContext() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>> h = getHandlerSpecialContext();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertFalse(r.get(1).isResult());
        Assertions.assertFalse(r.get(2).isResult());

    }


    @Test
    void filterItemsWithContextWithHistory() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>> h = getHandlerSpecialContext2();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertEquals(r.get(0).getItem().getInfo(), items.get(0).getInfo());
        Assertions.assertTrue(r.get(1).isResult());
        Assertions.assertEquals(r.get(1).getItem().getInfo(), items.get(1).getInfo());
        Assertions.assertTrue(r.get(2).isResult());
        Assertions.assertEquals(r.get(2).getItem().getInfo(), items.get(2).getInfo());

    }


    @NotNull
    private static Handler<ExampleItem, ListChainContext<ExampleItem>> getHandler() {
        return new Handler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem> ruleContext) {

                return Mono.just(FilterHandlerResult.SUCCESS);
            }
        };
    }



    @NotNull
    private static Handler<ExampleItem, ListChainContext<ExampleItem>> getHandlerSpecialContext() {
        return new Handler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem> ruleContext) {

                return Mono.just(item.getInfo().equals("info1")?FilterHandlerResult.SUCCESS:FilterHandlerResult.FAIL);
            }
        };
    }



    @NotNull
    private static Handler<ExampleItem, ListChainContext<ExampleItem>> getHandlerSpecialContext2() {
        return new Handler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem> ruleContext) {

                // questo filtro controlla che nel contesto ci siano i risultati precedenti

                if (ruleContext.getActualResults().size() == 0 && item.getInfo().equals("info1"))
                    return Mono.just(FilterHandlerResult.SUCCESS);
                if (ruleContext.getActualResults().size() == 1
                        && ruleContext.getActualResults().get(0).getItem().getInfo().equals("info1")
                        && ruleContext.getActualResults().get(0).isResult()
                        && item.getInfo().equals("info2"))
                    return Mono.just(FilterHandlerResult.SUCCESS);
                if (ruleContext.getActualResults().size() == 2
                        && ruleContext.getActualResults().get(0).getItem().getInfo().equals("info1")
                        && ruleContext.getActualResults().get(0).isResult()
                        && ruleContext.getActualResults().get(1).getItem().getInfo().equals("info2")
                        && ruleContext.getActualResults().get(1).isResult()
                        && item.getInfo().equals("info3"))
                    return Mono.just(FilterHandlerResult.SUCCESS);

                return Mono.just(FilterHandlerResult.FAIL);
            }
        };
    }

}