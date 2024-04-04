package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.FilterHandlerResult;
import it.pagopa.pn.commons.rules.model.FilterHandlerResultEnum;
import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListFilterChainResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

class ListChainEngineHandlerTest {


    ListChainEngineHandler<ExampleItem, ExampleContext> listChainEngineHandler;
    @BeforeEach
    void setup() {
        listChainEngineHandler = new ListChainEngineHandler<>();

    }
    @Test
    void filterItemsTrue() {
        // GIVEN
        ListChainHandler<ExampleItem, ExampleContext> h = getHandler();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ExampleContext context = ExampleContext.builder()
                .contextdata("somedata")
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isSuccess());
        Assertions.assertTrue(r.get(1).isSuccess());
        Assertions.assertTrue(r.get(2).isSuccess());
    }

    @Test
    void filterItemsOneElement() {
        // GIVEN
        ListChainHandler<ExampleItem, ExampleContext> h = getHandler();

        List<ExampleItem> items = List.of(new ExampleItem("info1"));

        ExampleContext context = ExampleContext.builder()
                .contextdata("somedata")
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isSuccess());

    }


    @Test
    void filterItemsWithContext() {
        // GIVEN
        ListChainHandler<ExampleItem, ExampleContext> h = getHandlerSpecialContext();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ExampleContext context = ExampleContext.builder()
                .contextdata("somedata")
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isSuccess());
        Assertions.assertFalse(r.get(1).isSuccess());
        Assertions.assertFalse(r.get(2).isSuccess());

    }


    @Test
    void filterItemsWithContextWithHistory() {
        // GIVEN
        ListChainHandler<ExampleItem, ExampleContext> h = getHandlerSpecialContext2();

        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ExampleContext context = ExampleContext.builder()
                .contextdata("somedata")
                .build();


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listChainEngineHandler.filterItems(context, items, h).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isSuccess());
        Assertions.assertEquals(r.get(0).getItem().getInfo(), items.get(0).getInfo());
        Assertions.assertTrue(r.get(1).isSuccess());
        Assertions.assertEquals(r.get(1).getItem().getInfo(), items.get(1).getInfo());
        Assertions.assertTrue(r.get(2).isSuccess());
        Assertions.assertEquals(r.get(2).getItem().getInfo(), items.get(2).getInfo());

    }


    @NotNull
    private static ListChainHandler<ExampleItem, ExampleContext> getHandler() {
        return new ListChainHandler<>() {

            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem, ExampleContext>  ruleContext) {

                return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, null,null));
            }
        };
    }



    @NotNull
    private static ListChainHandler<ExampleItem, ExampleContext> getHandlerSpecialContext() {
        return new ListChainHandler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem, ExampleContext> ruleContext) {

                return Mono.just(item.getInfo().equals("info1")?new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, null,null):new FilterHandlerResult(FilterHandlerResultEnum.FAIL, null,null));
            }
        };
    }



    @NotNull
    private static ListChainHandler<ExampleItem, ExampleContext> getHandlerSpecialContext2() {
        return new ListChainHandler<>() {
            @Override
            public Mono<FilterHandlerResult> filter(ExampleItem item, ListChainContext<ExampleItem, ExampleContext> ruleContext) {

                // questo filtro controlla che nel contesto ci siano i risultati precedenti

                if (ruleContext.getActualResults().size() == 0 && item.getInfo().equals("info1"))
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, null,null));
                if (ruleContext.getActualResults().size() == 1
                        && ruleContext.getActualResults().get(0).getItem().getInfo().equals("info1")
                        && ruleContext.getActualResults().get(0).isSuccess()
                        && item.getInfo().equals("info2"))
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, null,null));
                if (ruleContext.getActualResults().size() == 2
                        && ruleContext.getActualResults().get(0).getItem().getInfo().equals("info1")
                        && ruleContext.getActualResults().get(0).isSuccess()
                        && ruleContext.getActualResults().get(1).getItem().getInfo().equals("info2")
                        && ruleContext.getActualResults().get(1).isSuccess()
                        && item.getInfo().equals("info3"))
                    return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.SUCCESS, null,null));

                return Mono.just(new FilterHandlerResult(FilterHandlerResultEnum.FAIL, null,null));
            }
        };
    }

}