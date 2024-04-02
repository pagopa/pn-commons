package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListChainResultFilter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

class ListChainEngineHandlerTest {


    ListChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> listChainEngineHandler;
    @BeforeEach
    void setup() {
        SimpleChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> simpleChainEngineHandler = new SimpleChainEngineHandler<>();
        listChainEngineHandler = new ListChainEngineHandler<>(simpleChainEngineHandler);

    }
    @Test
    void filterItemsTrue() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> h = getHandler();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListChainResultFilter<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertTrue(r.get(1).isResult());
        Assertions.assertTrue(r.get(2).isResult());
    }

    @Test
    void filterItemsOneElement() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> h = getHandler();

        List<ExampleItem> items = List.of(new ExampleItem("info1"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListChainResultFilter<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());

    }


    @Test
    void filterItemsWithContext() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> h = getHandlerSpecialContext();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListChainResultFilter<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertFalse(r.get(1).isResult());
        Assertions.assertFalse(r.get(2).isResult());

    }


    @Test
    void filterItemsWithContextWithHistory() {
        // GIVEN
        Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> h = getHandlerSpecialContext2();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();


        // WHEN
        List<ListChainResultFilter<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

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
    private static Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> getHandler() {
        return new Handler<>() {
            @Override
            Mono<ListChainResultFilter<ExampleItem>> filter(ExampleItem item, ListChainContext<ExampleItem> ruleContext) {

                return Mono.just(new ListChainResultFilter<>(item, true));
            }
        };
    }



    @NotNull
    private static Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> getHandlerSpecialContext() {
        return new Handler<>() {
            @Override
            Mono<ListChainResultFilter<ExampleItem>> filter(ExampleItem item, ListChainContext<ExampleItem> ruleContext) {

                return Mono.just(new ListChainResultFilter<>(item, item.getInfo().equals("info1")));
            }
        };
    }



    @NotNull
    private static Handler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> getHandlerSpecialContext2() {
        return new Handler<>() {
            @Override
            Mono<ListChainResultFilter<ExampleItem>> filter(ExampleItem item, ListChainContext<ExampleItem> ruleContext) {

                // questo filtro controlla che nel contesto ci siano i risultati precedenti

                if (ruleContext.getActualResults().size() == 0 && item.getInfo().equals("info1"))
                    return Mono.just(new ListChainResultFilter<>(item, true));
                if (ruleContext.getActualResults().size() == 1
                        && ruleContext.getActualResults().get(0).getItem().getInfo().equals("info1")
                        && ruleContext.getActualResults().get(0).isResult()
                        && item.getInfo().equals("info2"))
                    return Mono.just(new ListChainResultFilter<>(item, true));
                if (ruleContext.getActualResults().size() == 2
                        && ruleContext.getActualResults().get(0).getItem().getInfo().equals("info1")
                        && ruleContext.getActualResults().get(0).isResult()
                        && ruleContext.getActualResults().get(1).getItem().getInfo().equals("info2")
                        && ruleContext.getActualResults().get(1).isResult()
                        && item.getInfo().equals("info3"))
                    return Mono.just(new ListChainResultFilter<>(item, true));

                return Mono.just(new ListChainResultFilter<>(item, false));
            }
        };
    }

}