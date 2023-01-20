package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        TransactWriteItemsEnhancedRequest request = TransactWriteItemsEnhancedRequest.builder().build();
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
}
