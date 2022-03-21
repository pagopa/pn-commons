package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.KeyValueStore;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

public abstract class AbstractDynamoKeyValueStore <T> implements KeyValueStore<Key,T> {
    protected final DynamoDbTable<T> table;

    protected AbstractDynamoKeyValueStore(DynamoDbTable<T> table) {
        this.table = table;
    }

    @Override
    public void put(T value) {
        table.putItem(value);
    }

    @Override
    public Optional<T> get(Key key) {
        return Optional.ofNullable(table.getItem(key));
    }

    @Override
    public void delete(Key key) {
        table.deleteItem(key);
    }
}
