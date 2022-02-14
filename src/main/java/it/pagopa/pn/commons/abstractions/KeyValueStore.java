package it.pagopa.pn.commons.abstractions;

import java.util.Optional;

public interface KeyValueStore<K,V> {

    void put(V value);

    void putIfAbsent(V value) throws IdConflictException;

    Optional<V> get(K key);

    void delete(K key);
}
