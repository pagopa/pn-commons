package it.pagopa.pn.commons.utils.dynamodb.async;

import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EqualsAndHashCode
@CustomLog
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
        request.transactWriteItems().forEach(log::logTransactionDynamoDBEntity);
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbEnhancedAsyncClient.transactWriteItems(request)
                .thenApply(queryResponse -> MDCUtils.enrichWithMDC(queryResponse, copyOfContextMap));
    }

    @Override
    public BatchGetResultPagePublisher batchGetItem(BatchGetItemEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return BatchGetResultPagePublisher.create(SdkPublisher.adapt(
                Mono.from(this.dynamoDbEnhancedAsyncClient.batchGetItem(request))
                        .doOnNext(response -> MDCUtils.enrichWithMDC(response, copyOfContextMap))));
    }

    @Override
    public CompletableFuture<BatchWriteResult> batchWriteItem(BatchWriteItemEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return dynamoDbEnhancedAsyncClient.batchWriteItem(request)
                .thenApply(response -> MDCUtils.enrichWithMDC(response, copyOfContextMap));
    }

    @Override
    public CompletableFuture<BatchWriteResult> batchWriteItem(Consumer<BatchWriteItemEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return dynamoDbEnhancedAsyncClient.batchWriteItem(requestConsumer)
                .thenApply(response -> MDCUtils.enrichWithMDC(response, copyOfContextMap));
    }

}
