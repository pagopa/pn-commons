package it.pagopa.pn.commons.abstractions;

import java.util.Optional;

public interface KeyValueStoreNew<T,K> {
    void put(T value);

    void putIfAbsent(T value, K key) throws IdConflictException;

    Optional<T> get(K key);

    void delete(K key);
}
