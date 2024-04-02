package it.pagopa.pn.commons.rules.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
/**
 * Risultato del filtro per una lista, rispetto a ResultFilter contiene anche l'item
 */
public class ListChainResultFilter<T extends Serializable> extends ResultFilter implements Serializable {

    public ListChainResultFilter(T item, boolean result) {
        super(result);

        this.item = item;
    }

    private T item;
}
