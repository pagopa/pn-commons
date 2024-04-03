package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListFilterChainResult;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ListChainContextExampleItem implements ListChainContext<ExampleItem> {

    private final List<ExampleItem> items;

    private final List<ListFilterChainResult<ExampleItem>> actualResults = new ArrayList<>();


    private String contextdata;

    @Override
    public void addResult(ListFilterChainResult<ExampleItem> result) {
        actualResults.add(result);
    }
}
