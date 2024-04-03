package it.pagopa.pn.commons.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
    @Test
    void filterItemFalse() {
        ChainHandler<Object, Object, FilterChainResult> h = new ChainHandler<>() {
            @Override
            Mono<FilterChainResult> filter(Object item, Object ruleContext) {

                return Mono.just(new FilterChainResult(false));
            }
        };

        FilterChainResult r = simpleChainEngineHandler.filterItem(new Object(), new Object(), h).block();

        Assertions.assertFalse(r.isResult());
    }


    @Test
    void filterItem_Exception() {
        ChainHandler<Object, Object, FilterChainResult> h = new ChainHandler<>() {
            @Override
            Mono<FilterChainResult> filter(Object item, Object ruleContext) {

                return Mono.error(new PnInternalException("errore", "errore!"));
            }
        };

        Mono<FilterChainResult> mono = simpleChainEngineHandler.filterItem(new Object(), new Object(), h);

        Assertions.assertThrows(PnInternalException.class, mono::block);
    }

*/
@Data
@AllArgsConstructor
@NoArgsConstructor
class ExampleItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String info;
}
