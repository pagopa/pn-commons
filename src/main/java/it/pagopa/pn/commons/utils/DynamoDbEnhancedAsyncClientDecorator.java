package it.pagopa.pn.commons.utils;

import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EqualsAndHashCode
public class DynamoDbEnhancedAsyncClientDecorator implements DynamoDbEnhancedAsyncClient {

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    public DynamoDbEnhancedAsyncClientDecorator(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
    }

    @Override
    public <T> DynamoDbAsyncTable<T> table(String s, TableSchema<T> tableSchema) {
        return new DynamoDbAsyncTableDecorator<>(this.dynamoDbEnhancedAsyncClient.table(s, tableSchema));
    }

    @Override
    public CompletableFuture<Void> transactWriteItems(Consumer<TransactWriteItemsEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbEnhancedAsyncClient.transactWriteItems(requestConsumer)
                .thenApply(queryResponse -> MDCUtils.enrichWithMDC(queryResponse, copyOfContextMap));
    }

    @Override
    public CompletableFuture<Void> transactWriteItems(TransactWriteItemsEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbEnhancedAsyncClient.transactWriteItems(request)
                .thenApply(queryResponse -> MDCUtils.enrichWithMDC(queryResponse, copyOfContextMap));
    }
}
