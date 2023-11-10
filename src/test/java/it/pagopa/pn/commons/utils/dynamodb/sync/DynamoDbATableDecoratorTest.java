package it.pagopa.pn.commons.utils.dynamodb.sync;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DynamoDbATableDecoratorTest {

    private DynamoDbTableDecorator<String> dynamoDbTableDecorator;

    private DynamoDbTable<String> delegate;

    @BeforeEach
    public void init() {
        delegate = Mockito.mock(DynamoDbTable.class);
        Mockito.when(delegate.tableName()).thenReturn("DYNAMODB_TABLE_NAME");
        dynamoDbTableDecorator = new DynamoDbTableDecorator<>(delegate);
    }

    @Test
    void mapperExtensionTest() {
        DynamoDbEnhancedClientExtension expectedValue = delegate.mapperExtension();
        Mockito.when(delegate.mapperExtension()).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.mapperExtension()).isEqualTo(expectedValue);
    }

    @Test
    void tableSchemaTest() {
        TableSchema<String> expectedValue = delegate.tableSchema();
        Mockito.when(delegate.tableSchema()).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.tableSchema()).isEqualTo(expectedValue);
    }

    @Test
    void indexTest() {
        DynamoDbIndex<String> index = Mockito.mock(DynamoDbIndex.class);
        Mockito.when(delegate.index("INDEX")).thenReturn(index);
        assertThat(dynamoDbTableDecorator.index("INDEX")).isEqualTo(index);
    }

    @Test
    void tableNameTest() {
        Mockito.when(delegate.tableName()).thenReturn("MANDATE");
        assertThat(dynamoDbTableDecorator.tableName()).isEqualTo("MANDATE");
    }

    @Test
    void keyFromTest() {
        Key key = delegate.keyFrom("A");
        Mockito.when(delegate.keyFrom("A")).thenReturn(key);
        assertThat(dynamoDbTableDecorator.keyFrom("A")).isEqualTo(key);
    }

    @Test
    void queryTest() {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder().build();
        PageIterable<String> pageIterable = Mockito.mock(PageIterable.class);
        Mockito.when(delegate.query(request)).thenReturn(pageIterable);
        Assertions.assertDoesNotThrow(() -> dynamoDbTableDecorator.query(request));
    }

    @Test
    void queryConsumerTest() {
        Consumer<QueryEnhancedRequest.Builder> consumer = QueryEnhancedRequest.Builder::build;
        PageIterable<String> pageIterable = Mockito.mock(PageIterable.class);
        Mockito.when(delegate.query(consumer)).thenReturn(pageIterable);
        Assertions.assertDoesNotThrow(() -> dynamoDbTableDecorator.query(consumer));
    }

    @Test
    void queryConditionalConsumerTest() {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(1).build());
        PageIterable<String> pageIterable = Mockito.mock(PageIterable.class);
        Mockito.when(delegate.query(queryConditional)).thenReturn(pageIterable);
        Assertions.assertDoesNotThrow(() -> dynamoDbTableDecorator.query(queryConditional));
    }

    @Test
    void putItemTest() {
        String entity = "anEntity";
        PutItemEnhancedRequest<String> request = PutItemEnhancedRequest.builder(String.class)
                .item(entity)
                .build();
        Assertions.assertDoesNotThrow(() -> dynamoDbTableDecorator.putItem(request));
    }

    @Test
    void putItemConsumerTest() {
        String entity = "anEntity";
        Consumer<PutItemEnhancedRequest.Builder<String>> consumer = (builder) -> builder.item(entity);
        Assertions.assertDoesNotThrow(() -> dynamoDbTableDecorator.putItem(consumer));
    }

    @Test
    void putItemGenericTest() {
        String request = "REQUEST";
        Assertions.assertDoesNotThrow(() -> dynamoDbTableDecorator.putItem(request));
    }

    @Test
    void getItemTest() {
        String request = "REQUEST";
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.getItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void getItemKeyTest() {
        Key request = Key.builder().partitionValue(1).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.getItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void getItemConsumerTest() {
        Consumer<GetItemEnhancedRequest.Builder> consumer = GetItemEnhancedRequest.Builder::build;
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(consumer)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.getItem(consumer)).isEqualTo(expectedValue);
    }

    @Test
    void getItemEnhancedRequestTest() {
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder().key(Key.builder().partitionValue("aKey").build()).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.getItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.getItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemTest() {
        String request = "REQUEST";
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.deleteItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemKeyTest() {
        Key request = Key.builder().partitionValue(1).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.deleteItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemConsumerTest() {
        Consumer<DeleteItemEnhancedRequest.Builder> consumer = DeleteItemEnhancedRequest.Builder::build;
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(consumer)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.deleteItem(consumer)).isEqualTo(expectedValue);
    }

    @Test
    void deleteItemEnhancedRequestTest() {
        DeleteItemEnhancedRequest request = DeleteItemEnhancedRequest.builder().key(Key.builder().partitionValue("aKey").build()).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.deleteItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.deleteItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void updateItemTest() {
        String request = "REQUEST";
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.updateItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.updateItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void updateItemConsumerTest() {
        Consumer<UpdateItemEnhancedRequest.Builder<String>> consumer = UpdateItemEnhancedRequest.Builder::build;
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.updateItem(consumer)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.updateItem(consumer)).isEqualTo(expectedValue);
    }

    @Test
    void updateItemEnhancedRequestTest() {
        UpdateItemEnhancedRequest<String> request = UpdateItemEnhancedRequest.builder(String.class).build();
        String expectedValue = "RESPONSE";
        Mockito.when(delegate.updateItem(request)).thenReturn(expectedValue);
        assertThat(dynamoDbTableDecorator.updateItem(request)).isEqualTo(expectedValue);
    }

    @Test
    void scanTest() {
        PageIterable<String> pageIterable = Mockito.mock(PageIterable.class);
        Mockito.when(delegate.scan()).thenReturn(pageIterable);
        assertThat(dynamoDbTableDecorator.scan()).isEqualTo(pageIterable);
    }

    @Test
    void scanWithRequestTest() {
        ScanEnhancedRequest request = ScanEnhancedRequest.builder().limit(10).build();
        PageIterable<String> pageIterable = Mockito.mock(PageIterable.class);
        Mockito.when(delegate.scan(request)).thenReturn(pageIterable);
        assertThat(dynamoDbTableDecorator.scan(request)).isEqualTo(pageIterable);
    }

    @Test
    void scanWithConsumerRequestTest() {
        Consumer<ScanEnhancedRequest.Builder> requestConsumer = builder -> builder.limit(10);
        PageIterable<String> pageIterable = Mockito.mock(PageIterable.class);
        Mockito.when(delegate.scan(requestConsumer)).thenReturn(pageIterable);
        assertThat(dynamoDbTableDecorator.scan(requestConsumer)).isEqualTo(pageIterable);
    }

}
