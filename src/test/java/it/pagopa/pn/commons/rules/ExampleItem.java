package it.pagopa.pn.commons.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
    @Test
    void filterItemFalse() {
        Handler<Object, Object, ResultFilter> h = new Handler<>() {
            @Override
            Mono<ResultFilter> filter(Object item, Object ruleContext) {

                return Mono.just(new ResultFilter(false));
            }
        };

        ResultFilter r = simpleChainEngineHandler.filterItem(new Object(), new Object(), h).block();

        Assertions.assertFalse(r.isResult());
    }


    @Test
    void filterItem_Exception() {
        Handler<Object, Object, ResultFilter> h = new Handler<>() {
            @Override
            Mono<ResultFilter> filter(Object item, Object ruleContext) {

                return Mono.error(new PnInternalException("errore", "errore!"));
            }
        };

        Mono<ResultFilter> mono = simpleChainEngineHandler.filterItem(new Object(), new Object(), h);

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
