package it.pagopa.pn.commons.utils.dynamodb.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DynamoDbEnhancedAsyncClientDecoratorTest {

    private DynamoDbEnhancedAsyncClientDecorator dynamoDbEnhancedAsyncClientDecorator;

    private DynamoDbEnhancedAsyncClient delegate;

    @BeforeEach
    public void init() {
        delegate = Mockito.mock(DynamoDbEnhancedAsyncClient.class);
        dynamoDbEnhancedAsyncClientDecorator = new DynamoDbEnhancedAsyncClientDecorator(delegate);
    }

    @Test
    void tableTest() {
        TableSchema<String> tableSchema = Mockito.mock(TableSchema.class);
        DynamoDbAsyncTable<String> mandateTable = delegate.table("MANDATE", tableSchema);
        Mockito.when(delegate.table("MANDATE", tableSchema)).thenReturn(mandateTable);
        assertThat(dynamoDbEnhancedAsyncClientDecorator.table("MANDATE", tableSchema)).isEqualTo(new DynamoDbAsyncTableDecorator<>(mandateTable));
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
        Mockito.when(delegate.transactWriteItems(request)).thenReturn(CompletableFuture.completedFuture(null));
        Assertions.assertDoesNotThrow(() -> dynamoDbEnhancedAsyncClientDecorator.transactWriteItems(request));
    }

    @Test
    void transactWriteItemsConsumerTest() {
        Consumer<TransactWriteItemsEnhancedRequest.Builder> consumer = TransactWriteItemsEnhancedRequest.Builder::build;
        Mockito.when(delegate.transactWriteItems(consumer)).thenReturn(CompletableFuture.completedFuture(null));
        Assertions.assertDoesNotThrow(() -> dynamoDbEnhancedAsyncClientDecorator.transactWriteItems(consumer));
    }

    @Test
    void batchGetItemTest() {
        BatchGetItemEnhancedRequest request = BatchGetItemEnhancedRequest.builder().build();
        Mockito.when(delegate.batchGetItem(request)).thenReturn(BatchGetResultPagePublisher.create(subscriber -> {

        }));
        Assertions.assertDoesNotThrow(() -> dynamoDbEnhancedAsyncClientDecorator.batchGetItem(request));
    }

    @Test
    void batchWriteItemTest() {
        BatchWriteItemEnhancedRequest request = BatchWriteItemEnhancedRequest.builder().build();
        BatchWriteResult result = BatchWriteResult.builder().unprocessedRequests(Map.of()).build();
        Mockito.when(delegate.batchWriteItem(request)).thenReturn(CompletableFuture.completedFuture(result));
        assertDoesNotThrow(() -> dynamoDbEnhancedAsyncClientDecorator.batchWriteItem(request));
    }

    @Test
    void batchWriteItemConsumerTest() {
        Consumer<BatchWriteItemEnhancedRequest.Builder> requestConsumer = BatchWriteItemEnhancedRequest.Builder::build;
        BatchWriteResult result = BatchWriteResult.builder().unprocessedRequests(Map.of()).build();
        Mockito.when(delegate.batchWriteItem(requestConsumer)).thenReturn(CompletableFuture.completedFuture(result));
        assertDoesNotThrow(() -> dynamoDbEnhancedAsyncClientDecorator.batchWriteItem(requestConsumer));
    }

}
