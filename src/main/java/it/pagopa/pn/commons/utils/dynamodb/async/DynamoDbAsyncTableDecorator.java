package it.pagopa.pn.commons.utils.dynamodb.async;

import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EqualsAndHashCode
@CustomLog
public class DynamoDbAsyncTableDecorator<T> implements DynamoDbAsyncTable<T> {

    private final DynamoDbAsyncTable<T> dynamoDbAsyncTable;

    public DynamoDbAsyncTableDecorator(DynamoDbAsyncTable<T> dynamoDbAsyncTable) {
        this.dynamoDbAsyncTable = dynamoDbAsyncTable;
    }

    @Override
    public DynamoDbAsyncIndex<T> index(String s) {
        DynamoDbAsyncIndex<T> index = this.dynamoDbAsyncTable.index(s);
        return new DynamoDbAsyncIndexDecorator<>(index);
    }

    @Override
    public DynamoDbEnhancedClientExtension mapperExtension() {
        return this.dynamoDbAsyncTable.mapperExtension();
    }

    @Override
    public TableSchema<T> tableSchema() {
        return this.dynamoDbAsyncTable.tableSchema();
    }

    @Override
    public String tableName() {
        return this.dynamoDbAsyncTable.tableName();
    }

    @Override
    public Key keyFrom(T t) {
        return this.dynamoDbAsyncTable.keyFrom(t);
    }

    @Override
    public PagePublisher<T> query(QueryEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        SdkPublisher<Page<T>> map = this.dynamoDbAsyncTable.query(request)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
        return PagePublisher.create(map);
    }

    @Override
    public PagePublisher<T> query(Consumer<QueryEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        SdkPublisher<Page<T>> publisherWithMDC = this.dynamoDbAsyncTable.query(requestConsumer)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
        return PagePublisher.create(publisherWithMDC);
    }

    @Override
    public PagePublisher<T> query(QueryConditional queryConditional) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        SdkPublisher<Page<T>> publisherWithMDC = this.dynamoDbAsyncTable.query(queryConditional)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
        return PagePublisher.create(publisherWithMDC);
    }

    @Override
    public CompletableFuture<Void> putItem(PutItemEnhancedRequest<T> request) {
        log.logPuttingDynamoDBEntity(dynamoDbAsyncTable.tableName(), request.item());
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.putItem(request)
                .thenApply(unused -> MDCUtils.enrichWithMDC(unused, copyOfContextMap))
                .thenApply(unused -> {
                    log.logPutDoneDynamoDBEntity(dynamoDbAsyncTable.tableName());
                    return unused;
                });
    }

    @Override
    public CompletableFuture<Void> putItem(Consumer<PutItemEnhancedRequest.Builder<T>> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.putItem(requestConsumer)
                .thenApply(unused -> MDCUtils.enrichWithMDC(unused, copyOfContextMap))
                .thenApply(unused -> {
                    log.logPutDoneDynamoDBEntity(dynamoDbAsyncTable.tableName());
                    return unused;
                });
    }

    @Override
    public CompletableFuture<Void> putItem(T item) {
        log.logPuttingDynamoDBEntity(dynamoDbAsyncTable.tableName(), item);
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.putItem(item)
                .thenApply(unused -> MDCUtils.enrichWithMDC(unused, copyOfContextMap))
                .thenApply(unused -> {
                    log.logPutDoneDynamoDBEntity(dynamoDbAsyncTable.tableName());
                    return unused;
                });
    }

    @Override
    public CompletableFuture<T> getItem(Key key) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.getItem(key)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logGetDynamoDBEntity(dynamoDbAsyncTable.tableName(), key, t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> getItem(T keyItem) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.getItem(keyItem)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logGetDynamoDBEntity(dynamoDbAsyncTable.tableName(), keyItem, t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> getItem(Consumer<GetItemEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.getItem(requestConsumer)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap));
    }

    @Override
    public CompletableFuture<T> getItem(GetItemEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.getItem(request)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logGetDynamoDBEntity(dynamoDbAsyncTable.tableName(), request.key(), t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> deleteItem(Consumer<DeleteItemEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.deleteItem(requestConsumer)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap));
    }

    @Override
    public CompletableFuture<T> deleteItem(DeleteItemEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.deleteItem(request)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logDeleteDynamoDBEntity(dynamoDbAsyncTable.tableName(), request.key(), t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> deleteItem(Key key) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.deleteItem(key)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logDeleteDynamoDBEntity(dynamoDbAsyncTable.tableName(), key, t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> deleteItem(T keyItem) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.deleteItem(keyItem)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logDeleteDynamoDBEntity(dynamoDbAsyncTable.tableName(), keyItem, t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> updateItem(T item) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.updateItem(item)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logUpdateDynamoDBEntity(dynamoDbAsyncTable.tableName(), t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> updateItem(Consumer<UpdateItemEnhancedRequest.Builder<T>> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.updateItem(requestConsumer)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logUpdateDynamoDBEntity(dynamoDbAsyncTable.tableName(), t);
                    return t;
                });
    }

    @Override
    public CompletableFuture<T> updateItem(UpdateItemEnhancedRequest<T> request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbAsyncTable.updateItem(request)
                .thenApply(t -> MDCUtils.enrichWithMDC(t, copyOfContextMap))
                .thenApply(t -> {
                    log.logUpdateDynamoDBEntity(dynamoDbAsyncTable.tableName(), t);
                    return t;
                });
    }

    @Override
    public PagePublisher<T> scan(ScanEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        SdkPublisher<Page<T>> map = this.dynamoDbAsyncTable.scan(request)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));

        return PagePublisher.create(map);
    }

    @Override
    public PagePublisher<T> scan(Consumer<ScanEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        SdkPublisher<Page<T>> map = this.dynamoDbAsyncTable.scan(requestConsumer)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));

        return PagePublisher.create(map);
    }

    @Override
    public PagePublisher<T> scan() {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        SdkPublisher<Page<T>> map = this.dynamoDbAsyncTable.scan()
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
        return PagePublisher.create(map);
    }

}
