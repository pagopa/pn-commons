package it.pagopa.pn.commons.rules;

import it.pagopa.pn.commons.rules.model.ListChainContext;
import it.pagopa.pn.commons.rules.model.ListChainResultFilter;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ListChainContextExampleItem implements ListChainContext<ExampleItem> {

    private final List<ExampleItem> items;

    private final List<ListChainResultFilter<ExampleItem>> actualResults = new ArrayList<>();


    private String contextdata;

    @Override
    public void addResult(ListChainResultFilter<ExampleItem> result) {
        actualResults.add(result);
    }
}
