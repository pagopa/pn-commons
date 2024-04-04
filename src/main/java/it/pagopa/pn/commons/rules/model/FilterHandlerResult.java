package it.pagopa.pn.commons.rules.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


/**
 * Risultato di un handler:
 * - SUCCESS: la catena di filtri termina immediatamente con esito positivo
 * - NEXT: lo step è positivo, si procedere con la valutazione dello step successivo (o ritorna esito positivo nel caso di ultimo step)
 * - FAIL: la catena di filtri termina immediatamente con esito negativo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterHandlerResult implements Serializable {

    @NotNull
    private FilterHandlerResultEnum result;

    private String code;

    private String diagnostic;

}
