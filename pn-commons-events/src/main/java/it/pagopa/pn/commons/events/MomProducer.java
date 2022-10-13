package it.pagopa.pn.commons.events;

import it.pagopa.pn.api.dto.events.GenericEvent;

import java.util.Collections;
import java.util.List;

public interface MomProducer<T extends GenericEvent> {

    void push(List<T> messages);

    default void push(T message) {
        push( Collections.singletonList( message ));
    }
}
