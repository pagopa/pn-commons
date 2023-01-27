package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.paginators.BatchGetItemPublisher;
import software.amazon.awssdk.services.dynamodb.paginators.QueryPublisher;
import software.amazon.awssdk.utils.builder.SdkBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DynamoDbAsyncClientDecoratorTest {

    private DynamoDbAsyncClientDecorator dynamoDbAsyncClientDecorator;

    private DynamoDbAsyncClient delegate;

    @BeforeEach
    public void init() {
        delegate = Mockito.mock(DynamoDbAsyncClient.class);
        dynamoDbAsyncClientDecorator = new DynamoDbAsyncClientDecorator(delegate);
    }

    @Test
    void serviceNameTest() {
        Mockito.when(delegate.serviceName()).thenReturn("SERVICE");
        assertThat(dynamoDbAsyncClientDecorator.serviceName()).isEqualTo("SERVICE");
    }

    @Test
    void closeTest() {
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncClientDecorator.close());
    }


    @Test
    void queryTest() throws ExecutionException, InterruptedException {
        QueryRequest mock = QueryRequest.builder().build();
        QueryResponse expectedValue = QueryResponse.builder().build();
        Mockito.when(delegate.query(mock)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.query(mock).get()).isEqualTo(expectedValue);
    }

    @Test
    void queryConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<QueryRequest.Builder> consumer = SdkBuilder::build;
        QueryResponse expectedValue = QueryResponse.builder().build();
        Mockito.when(delegate.query(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.query(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void queryPaginatorTest() {
        QueryRequest mock = QueryRequest.builder().build();
        QueryPublisher publisher = Mockito.mock(QueryPublisher.class);
        Mockito.when(delegate.queryPaginator(mock)).thenReturn(publisher);
        assertThat(dynamoDbAsyncClientDecorator.queryPaginator(mock)).isEqualTo(publisher);
    }

    @Test
    void queryPaginatorConsumerTest() {
        Consumer<QueryRequest.Builder> consumer = SdkBuilder::build;
        QueryPublisher publisher = Mockito.mock(QueryPublisher.class);
        Mockito.when(delegate.queryPaginator(consumer)).thenReturn(publisher);
        assertThat(dynamoDbAsyncClientDecorator.queryPaginator(consumer)).isEqualTo(publisher);
    }

    @Test
    void putItemTest() throws ExecutionException, InterruptedException {
        PutItemRequest putItemRequest = PutItemRequest.builder().build();
        PutItemResponse expectedValue = PutItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.putItem(putItemRequest)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.putItem(putItemRequest).get()).isEqualTo(expectedValue);
    }

    @Test
    void putItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<PutItemRequest.Builder> consumer = SdkBuilder::build;
        PutItemResponse expectedValue = PutItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.putItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.putItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void transactWriteItemsTest() throws ExecutionException, InterruptedException {
        TransactWriteItemsRequest request = TransactWriteItemsRequest.builder().build();
        TransactWriteItemsResponse expectedValue = TransactWriteItemsResponse.builder().consumedCapacity(ConsumedCapacity
                .builder().tableName("TABLE").build())
                .build();

        Mockito.when(delegate.transactWriteItems(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.transactWriteItems(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void transactWriteItemsConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<TransactWriteItemsRequest.Builder> consumer = SdkBuilder::build;
        TransactWriteItemsResponse expectedValue = TransactWriteItemsResponse.builder().consumedCapacity(ConsumedCapacity
                        .builder().tableName("TABLE").build())
                .build();

        Mockito.when(delegate.transactWriteItems(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.transactWriteItems(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void getItemTest() throws ExecutionException, InterruptedException {
        GetItemRequest request = GetItemRequest.builder().build();
        GetItemResponse expectedValue = GetItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.getItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.getItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void getItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<GetItemRequest.Builder> consumer = SdkBuilder::build;
        GetItemResponse expectedValue = GetItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.getItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.getItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemTest() throws ExecutionException, InterruptedException {
        DeleteItemRequest request = DeleteItemRequest.builder().build();
        DeleteItemResponse expectedValue = DeleteItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.deleteItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.deleteItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<DeleteItemRequest.Builder> consumer = SdkBuilder::build;
        DeleteItemResponse expectedValue = DeleteItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.deleteItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.deleteItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void updateItemTest() throws ExecutionException, InterruptedException {
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        UpdateItemResponse expectedValue = UpdateItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.updateItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.updateItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void updateItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<UpdateItemRequest.Builder> consumer = SdkBuilder::build;
        UpdateItemResponse expectedValue = UpdateItemResponse.builder().consumedCapacity(ConsumedCapacity.builder().tableName("TABLE").build()).build();
        Mockito.when(delegate.updateItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncClientDecorator.updateItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void batchGetItemPaginator() {
        BatchGetItemRequest request = BatchGetItemRequest.builder().build();
        BatchGetItemPublisher publisher = new BatchGetItemPublisher(null, null);
        Mockito.when(delegate.batchGetItemPaginator(request)).thenReturn(publisher);
        assertThat(dynamoDbAsyncClientDecorator.batchGetItemPaginator(request)).isEqualTo(publisher);
    }

}
