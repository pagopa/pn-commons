package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DynamoDbAsyncIndexDecoratorTest {

    private DynamoDbAsyncIndexDecorator<String> dynamoDbAsyncIndexDecorator;

    private DynamoDbAsyncIndex<String> delegate;

    @BeforeEach
    public void init() {
        delegate = Mockito.mock(DynamoDbAsyncIndex.class);
        dynamoDbAsyncIndexDecorator = new DynamoDbAsyncIndexDecorator<>(delegate);
    }

    @Test
    void mapperExtensionTest() {
        DynamoDbEnhancedClientExtension expectedValue = delegate.mapperExtension();
        Mockito.when(delegate.mapperExtension()).thenReturn(expectedValue);
        assertThat(dynamoDbAsyncIndexDecorator.mapperExtension()).isEqualTo(expectedValue);
    }

    @Test
    void tableSchemaTest() {
        TableSchema<String> expectedValue = delegate.tableSchema();
        Mockito.when(delegate.tableSchema()).thenReturn(expectedValue);
        assertThat(dynamoDbAsyncIndexDecorator.tableSchema()).isEqualTo(expectedValue);
    }

    @Test
    void indexNameTest() {
        Mockito.when(delegate.indexName()).thenReturn("INDICE");
        assertThat(dynamoDbAsyncIndexDecorator.indexName()).isEqualTo("INDICE");
    }

    @Test
    void tableNameTest() {
        Mockito.when(delegate.tableName()).thenReturn("MANDATE");
        assertThat(dynamoDbAsyncIndexDecorator.tableName()).isEqualTo("MANDATE");
    }

    @Test
    void keyFromTest() {
        Key key = delegate.keyFrom("A");
        Mockito.when(delegate.keyFrom("A")).thenReturn(key);
        assertThat(dynamoDbAsyncIndexDecorator.keyFrom("A")).isEqualTo(key);
    }

    @Test
    void queryTest() {
        QueryConditional mock = QueryConditional.keyEqualTo(Key.builder().partitionValue(1).build());
        SdkPublisher<Page<String>> sdkPublisher = SdkPublisher.adapt(Mono.just(Page.create(List.of(""))));
        Mockito.when(delegate.query(mock)).thenReturn(sdkPublisher);
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncIndexDecorator.query(mock));
    }

    @Test
    void queryConsumerTest() {
        Consumer<QueryEnhancedRequest.Builder> mock = Mockito.mock(Consumer.class);
        SdkPublisher<Page<String>> sdkPublisher = Mockito.mock(SdkPublisher.class);
        Mockito.when(delegate.query(mock)).thenReturn(sdkPublisher);
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncIndexDecorator.query(mock));
    }

    @Test
    void queryEnhancedRequestTest() {
        QueryEnhancedRequest mock = QueryEnhancedRequest.builder().build();
        SdkPublisher<Page<String>> sdkPublisher = Mockito.mock(SdkPublisher.class);
        Mockito.when(delegate.query(mock)).thenReturn(sdkPublisher);
        Assertions.assertDoesNotThrow(() -> dynamoDbAsyncIndexDecorator.query(mock));
    }

}
