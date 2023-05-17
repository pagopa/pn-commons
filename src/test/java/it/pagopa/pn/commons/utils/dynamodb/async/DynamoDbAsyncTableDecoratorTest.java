package it.pagopa.pn.commons.utils.dynamodb.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DynamoDbAsyncTableDecoratorTest {

    private DynamoDbAsyncTableDecorator<String> dynamoDbAsyncTableDecorator;

    private DynamoDbAsyncTable<String> delegate;

    @BeforeEach
    public void init() {
        delegate = Mockito.mock(DynamoDbAsyncTable.class);
        Mockito.when(delegate.tableName()).thenReturn("DYNAMODB_TABLE_NAME");
        dynamoDbAsyncTableDecorator = new DynamoDbAsyncTableDecorator<>(delegate);
    }

    @Test
    void mapperExtensionTest() {
        DynamoDbEnhancedClientExtension expectedValue = delegate.mapperExtension();
        Mockito.when(delegate.mapperExtension()).thenReturn(expectedValue);
        assertThat(dynamoDbAsyncTableDecorator.mapperExtension()).isEqualTo(expectedValue);
    }

    @Test
    void tableSchemaTest() {
        TableSchema<String> expectedValue = delegate.tableSchema();
        Mockito.when(delegate.tableSchema()).thenReturn(expectedValue);
        assertThat(dynamoDbAsyncTableDecorator.tableSchema()).isEqualTo(expectedValue);
    }

    @Test
    void indexTest() {
        DynamoDbAsyncIndex<String> index = Mockito.mock(DynamoDbAsyncIndex.class);
        Mockito.when(delegate.index("INDEX")).thenReturn(index);
        assertThat(dynamoDbAsyncTableDecorator.index("INDEX")).isEqualTo(new DynamoDbAsyncIndexDecorator<>(index));
    }

    @Test
    void tableNameTest() {
        Mockito.when(delegate.tableName()).thenReturn("MANDATE");
        assertThat(dynamoDbAsyncTableDecorator.tableName()).isEqualTo("MANDATE");
    }

    @Test
    void keyFromTest() {
        Key key = delegate.keyFrom("A");
        Mockito.when(delegate.keyFrom("A")).thenReturn(key);
        assertThat(dynamoDbAsyncTableDecorator.keyFrom("A")).isEqualTo(key);
    }

    @Test
    void queryTest() {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder().build();
        PagePublisher<String> publisher = Subscriber::onComplete;
        Mockito.when(delegate.query(request)).thenReturn(publisher);
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncTableDecorator.query(request));
    }

    @Test
    void queryConsumerTest() {
        Consumer<QueryEnhancedRequest.Builder> consumer = QueryEnhancedRequest.Builder::build;
        PagePublisher<String> publisher = Subscriber::onComplete;
        Mockito.when(delegate.query(consumer)).thenReturn(publisher);
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncTableDecorator.query(consumer));
    }

    @Test
    void queryConditionalConsumerTest() {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(1).build());
        PagePublisher<String> publisher = Subscriber::onComplete;
        Mockito.when(delegate.query(queryConditional)).thenReturn(publisher);
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncTableDecorator.query(queryConditional));
    }

    @Test
    void putItemTest() {
        String entity = "anEntity";
        PutItemEnhancedRequest<String> request = PutItemEnhancedRequest.builder(String.class)
                .item(entity)
                .build();
        Mockito.when(delegate.putItem(request)).thenReturn(CompletableFuture.completedFuture(null));
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncTableDecorator.putItem(request));
    }

    @Test
    void putItemConsumerTest() {
        String entity = "anEntity";
        Consumer<PutItemEnhancedRequest.Builder<String>> consumer = (builder) -> builder.item(entity);
        Mockito.when(delegate.putItem(consumer)).thenReturn(CompletableFuture.completedFuture(null));
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncTableDecorator.putItem(consumer));
    }

    @Test
    void putItemGenericTest() {
        String request = "REQUEST";
        Mockito.when(delegate.putItem(request)).thenReturn(CompletableFuture.completedFuture(null));
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncTableDecorator.putItem(request));
    }

    @Test
    void getItemTest() throws ExecutionException, InterruptedException {
        String request = "REQUEST";
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.getItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void getItemKeyTest() throws ExecutionException, InterruptedException {
        Key request = Key.builder().partitionValue(1).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.getItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void getItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<GetItemEnhancedRequest.Builder> consumer = GetItemEnhancedRequest.Builder::build;
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.getItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void getItemEnhancedRequestTest() throws ExecutionException, InterruptedException {
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder().key(Key.builder().partitionValue("aKey").sortValue("aSortKey").build()).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.getItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemTest() throws ExecutionException, InterruptedException {
        String request = "REQUEST";
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.deleteItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemKeyTest() throws ExecutionException, InterruptedException {
        Key request = Key.builder().partitionValue(1).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.deleteItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<DeleteItemEnhancedRequest.Builder> consumer = DeleteItemEnhancedRequest.Builder::build;
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.deleteItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemEnhancedRequestTest() throws ExecutionException, InterruptedException {
        DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder().build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.deleteItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void updateItemTest() throws ExecutionException, InterruptedException {
        String request = "REQUEST";
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.updateItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.updateItem(request).get()).isEqualTo(expectedValue);
    }

    @Test
    void updateItemConsumerTest() throws ExecutionException, InterruptedException {
        Consumer<UpdateItemEnhancedRequest.Builder<String>> consumer = UpdateItemEnhancedRequest.Builder::build;
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.updateItem(consumer)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.updateItem(consumer).get()).isEqualTo(expectedValue);
    }

    @Test
    void updateItemEnhancedRequestTest() throws ExecutionException, InterruptedException {
        UpdateItemEnhancedRequest<String> request = UpdateItemEnhancedRequest.builder(String.class).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.updateItem(request)).thenReturn(CompletableFuture.completedFuture(expectedValue));
        assertThat(dynamoDbAsyncTableDecorator.updateItem(request).get()).isEqualTo(expectedValue);
    }

}
