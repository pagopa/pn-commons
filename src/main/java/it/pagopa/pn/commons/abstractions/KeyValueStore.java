package it.pagopa.pn.commons.abstractions;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;

import java.util.Optional;

public interface KeyValueStore<K,V> {

    void put(V value);

    void putIfAbsent(V value) throws PnIdConflictException;

    Optional<V> get(K key);

    void delete(K key);
}
