package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AbstractDynamoKeyValueStoreTest {

    AbstractDynamoKeyValueStore abstractDynamoKeyValueStore;
    DynamoDbTable<Object> table;

    @BeforeEach
    public void init(){
        table = Mockito.mock(DynamoDbTable.class);

        abstractDynamoKeyValueStore = new AbstractDynamoKeyValueStore(table) {
            @Override
            public void putIfAbsent(Object value) throws PnIdConflictException {

            }
        };
    }

    @Test
    void put() {
        Object d = new Object();
        Mockito.doNothing().when(table).putItem(d);
        assertDoesNotThrow(() -> abstractDynamoKeyValueStore.put(d));
    }

    @Test
    void get() {
        Key d = Key.builder()
                .partitionValue("a").build();
        Mockito.when(table.getItem(d)).thenReturn(new Object());
        assertDoesNotThrow(() -> abstractDynamoKeyValueStore.get(d));
    }

    @Test
    void delete() {
        Key d = Key.builder()
                .partitionValue("a").build();
        Mockito.when(table.deleteItem(d)).thenReturn(new Object());
        assertDoesNotThrow(() -> abstractDynamoKeyValueStore.delete(d));
    }
}