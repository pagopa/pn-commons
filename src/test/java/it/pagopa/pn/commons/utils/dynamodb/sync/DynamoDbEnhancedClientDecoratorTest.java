package it.pagopa.pn.commons.utils.dynamodb.sync;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamoDbEnhancedClientDecoratorTest {

    private DynamoDbEnhancedClientDecorator dynamoDbEnhancedClientDecorator;

    private DynamoDbEnhancedClient delegate;

    @BeforeEach
    public void init() {
        delegate = Mockito.mock(DynamoDbEnhancedClient.class);
        dynamoDbEnhancedClientDecorator = new DynamoDbEnhancedClientDecorator(delegate);
    }

    @Test
    void tableTest() {
        TableSchema<String> tableSchema = Mockito.mock(TableSchema.class);
        DynamoDbTable<String> mandateTable = delegate.table("MANDATE", tableSchema);
        Mockito.when(delegate.table("MANDATE", tableSchema)).thenReturn(mandateTable);
        assertThat(dynamoDbEnhancedClientDecorator.table("MANDATE", tableSchema)).isEqualTo(new DynamoDbTableDecorator<>(mandateTable));
    }

    @Test
    void transactWriteItemsTest() {
        DynamoDbAsyncTable<String> table = Mockito.mock(DynamoDbAsyncTable.class);
        TableSchema<String> tableSchema = Mockito.mock(TableSchema.class);
        TableMetadata tableMetadata = Mockito.mock(TableMetadata.class);
        Mockito.when(tableSchema.tableMetadata()).thenReturn(tableMetadata);
        Mockito.when(table.tableName()).thenReturn("MANDATE");
        Mockito.when(table.tableSchema()).thenReturn(tableSchema);
        TransactWriteItemsEnhancedRequest.Builder builder = TransactWriteItemsEnhancedRequest.builder();
        builder.addDeleteItem(table, Key.builder().partitionValue("aKey").build());

        TransactWriteItemsEnhancedRequest request = builder.build();
        Assertions.assertDoesNotThrow(() -> dynamoDbEnhancedClientDecorator.transactWriteItems(request));
    }

    @Test
    void transactWriteItemsConsumerTest() {
        Consumer<TransactWriteItemsEnhancedRequest.Builder> consumer = TransactWriteItemsEnhancedRequest.Builder::build;
        Assertions.assertDoesNotThrow(() -> dynamoDbEnhancedClientDecorator.transactWriteItems(consumer));
    }

    @Test
    void batchGetItemTest() {
        BatchGetItemEnhancedRequest request = BatchGetItemEnhancedRequest.builder().build();
        BatchGetResultPageIterable result = Mockito.mock(BatchGetResultPageIterable.class);
        Mockito.when(delegate.batchGetItem(request)).thenReturn(result);
        assertEquals(result, dynamoDbEnhancedClientDecorator.batchGetItem(request));
    }

    @Test
    void batchWriteItemTest() {
        BatchWriteItemEnhancedRequest request = BatchWriteItemEnhancedRequest.builder().build();
        BatchWriteResult result = BatchWriteResult.builder().unprocessedRequests(Map.of()).build();
        Mockito.when(delegate.batchWriteItem(request)).thenReturn(result);
        assertEquals(result, dynamoDbEnhancedClientDecorator.batchWriteItem(request));
    }

    @Test
    void batchWriteItemConsumerTest() {
        Consumer<BatchWriteItemEnhancedRequest.Builder> requestConsumer = BatchWriteItemEnhancedRequest.Builder::build;
        BatchWriteResult result = BatchWriteResult.builder().unprocessedRequests(Map.of()).build();
        Mockito.when(delegate.batchWriteItem(requestConsumer)).thenReturn(result);
        assertEquals(result, dynamoDbEnhancedClientDecorator.batchWriteItem(requestConsumer));
    }

}
