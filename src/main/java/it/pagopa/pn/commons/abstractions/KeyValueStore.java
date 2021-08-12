package it.pagopa.pn.commons.abstractions;

public interface KeyValueStore<K,V> {

    void put(V value);

    void putIfAbsent(V value) throws IdConflictException;

    V get(K key);

    void delete(K key);
}
