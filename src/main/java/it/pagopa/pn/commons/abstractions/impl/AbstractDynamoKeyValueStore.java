package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStoreNew;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

public class AbstractDynamoKeyValueStore <T> implements KeyValueStoreNew<T, Key> {
    protected final DynamoDbTable<T> table;

    public AbstractDynamoKeyValueStore(DynamoDbTable<T> table) {
        this.table = table;
    }

    @Override
    public void put(T value) {
        table.putItem(value);
    }

    @Override
    public void putIfAbsent(T value, Key key) throws IdConflictException {
        if(get(key).isEmpty()){
            put(value);
        }else {
            throw new IdConflictException(key);
        }
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
