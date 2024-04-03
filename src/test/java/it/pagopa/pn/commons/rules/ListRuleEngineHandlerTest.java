package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListFilterChainResult;
import it.pagopa.pn.commons.rules.model.RuleModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ListRuleEngineHandlerTest {

    ListRuleEngineHandlerExampleItem listRuleEngineHandlerExampleItem;
    @BeforeEach
    void setup() {
        ListChainEngineHandler<ExampleItem, ExampleContext> listChainEngineHandler = new ListChainEngineHandler<>();

        listRuleEngineHandlerExampleItem = new ListRuleEngineHandlerExampleItem(listChainEngineHandler);

    }
    @Test
    void filterItems() {

        // GIVEN
        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ExampleContext context = ExampleContext.builder()
                .contextdata("somedata")
                .build();

        List<RuleModel> rules = List.of(() -> "regola1", () -> "regola2");


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listRuleEngineHandlerExampleItem.filterItems(context, items, rules).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertFalse(r.get(0).isResult());
        Assertions.assertFalse(r.get(1).isResult());
        Assertions.assertFalse(r.get(2).isResult());

    }

    @Test
    void filterItemsTrue() {

        // GIVEN
        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ExampleContext context = ExampleContext.builder()
                .contextdata("somedata")
                .build();

        List<RuleModel> rules = List.of(() -> "regola1", () -> "regola1");


        // WHEN
        List<ListFilterChainResult<ExampleItem>> r = listRuleEngineHandlerExampleItem.filterItems(context, items, rules).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertTrue(r.get(1).isResult());
        Assertions.assertTrue(r.get(2).isResult());

    }
}