package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListChainResultFilter;
import it.pagopa.pn.commons.rules.model.RuleModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListRuleEngineHandlerTest {

    ListRuleEngineHandlerExampleItem listRuleEngineHandlerExampleItem;
    @BeforeEach
    void setup() {
        SimpleChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> simpleChainEngineHandler = new SimpleChainEngineHandler<>();
        ListChainEngineHandler<ExampleItem, ListChainContext<ExampleItem>, ListChainResultFilter<ExampleItem>> listChainEngineHandler = new ListChainEngineHandler<>(simpleChainEngineHandler);

        listRuleEngineHandlerExampleItem = new ListRuleEngineHandlerExampleItem(listChainEngineHandler);

    }
    @Test
    void filterItems() {

        // GIVEN
        List<ExampleItem> items = List.of(new ExampleItem("info1"),new ExampleItem("info2"),new ExampleItem("info3"));

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();

        List<RuleModel> rules = List.of(() -> "regola1", () -> "regola2");


        // WHEN
        List<ListChainResultFilter<ExampleItem>> r = listRuleEngineHandlerExampleItem.filterItems(context, items, rules).collectList().block();

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

        ListChainContext<ExampleItem> context = ListChainContextExampleItem.builder()
                .contextdata("somedata")
                .items(items)
                .build();

        List<RuleModel> rules = List.of(() -> "regola1", () -> "regola1");


        // WHEN
        List<ListChainResultFilter<ExampleItem>> r = listRuleEngineHandlerExampleItem.filterItems(context, items, rules).collectList().block();

        // THEN
        Assertions.assertEquals(items.size(), r.size());
        Assertions.assertTrue(r.get(0).isResult());
        Assertions.assertTrue(r.get(1).isResult());
        Assertions.assertTrue(r.get(2).isResult());

    }
}