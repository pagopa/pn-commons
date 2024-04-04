package it.pagopa.pn.commons.rules.model;

import java.io.Serializable;


/**
 * Risultato di un handler:
 * - SUCCESS: la catena di filtri termina immediatamente con esito positivo
 * - NEXT: lo step è positivo, si procedere con la valutazione dello step successivo (o ritorna esito positivo nel caso di ultimo step)
 * - FAIL: la catena di filtri termina immediatamente con esito negativo
 */
public enum FilterHandlerResultEnum implements Serializable {
    SUCCESS,
    NEXT,
    FAIL;

}
