package it.pagopa.pn.commons.db;

import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@CustomLog
public abstract class BaseDAO<T> {

    @Getter
    @Setter
    @AllArgsConstructor
    protected static class Keys {
        Key from;
        Key to;
    }

    protected final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    protected final DynamoDbAsyncClient dynamoDbAsyncClient;
    protected final DynamoDbAsyncTable<T> dynamoTable;
    protected final String table;
    private static final int MAX_DYNAMODB_BATCH_SIZE = 100;

    private final Class<T> tClass;


    protected BaseDAO(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                      DynamoDbAsyncClient dynamoDbAsyncClient, String tableName, Class<T> tClass) {
        this.dynamoTable = dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(tClass));
        this.table = tableName;
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
        this.tClass = tClass;
    }

    protected CompletableFuture<T> put(T entity){
        log.logPuttingDynamoDBEntity(dynamoTable.tableName(), entity);
        PutItemEnhancedRequest<T> putRequest = PutItemEnhancedRequest.builder(tClass)
                .item(entity)
                .build();
        return dynamoTable.putItem(putRequest).thenApply(x -> {
            log.logPutDoneDynamoDBEntity(dynamoTable.tableName());
            return entity;
        });
    }

    protected CompletableFuture<T> delete(String partitionKey, String sortKey){
        Key.Builder keyBuilder = Key.builder().partitionValue(partitionKey);
        if (!StringUtils.isBlank(sortKey)){
            keyBuilder.sortValue(sortKey);
        }
        return dynamoTable.deleteItem(keyBuilder.build()).thenApply(t -> {
            log.logDeleteDynamoDBEntity(dynamoTable.tableName(), keyBuild(partitionKey, sortKey), t);
            return t;
        });
    }

    protected CompletableFuture<Void> putWithTransact(TransactWriteItemsEnhancedRequest transactRequest){
        transactRequest.transactWriteItems().forEach(log::logTransactionDynamoDBEntity);
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.dynamoDbEnhancedAsyncClient.transactWriteItems(transactRequest)
                .thenApply(queryResponse -> MDCUtils.enrichWithMDC(queryResponse, copyOfContextMap));
    }

    protected CompletableFuture<T> update(T entity){
        UpdateItemEnhancedRequest<T> updateRequest = UpdateItemEnhancedRequest
                .builder(tClass).item(entity).build();
        return dynamoTable.updateItem(updateRequest).thenApply(t -> {
            log.logUpdateDynamoDBEntity(dynamoTable.tableName(), t);
            return t;
        });
    }

    protected CompletableFuture<T> get(String partitionKey, String sortKey){
        Key.Builder keyBuilder = Key.builder().partitionValue(partitionKey);
        if (!StringUtils.isBlank(sortKey)){
            keyBuilder.sortValue(sortKey);
        }

        return dynamoTable.getItem(keyBuilder.build()).thenApply(data -> {
            log.logGetDynamoDBEntity(dynamoTable.tableName(), keyBuild(partitionKey, sortKey), data);
            return data;
        });
    }

    public Flux<T> getBySecondaryIndex(String index, String partitionKey, String sortKey){

        Key.Builder keyBuilder = Key.builder().partitionValue(partitionKey);
        if (!StringUtils.isBlank(sortKey)){
            keyBuilder.sortValue(sortKey);
        }

        return Flux.from(dynamoTable.index(index).query(QueryConditional.keyEqualTo(keyBuilder.build())).flatMapIterable(Page::items));
    }

    public Flux<T> getByFilter(QueryConditional conditional, String index, Map<String, AttributeValue> values, String filterExpression, Integer maxElements){
        QueryEnhancedRequest.Builder qeRequest = QueryEnhancedRequest
                .builder()
                .queryConditional(conditional);
        if (maxElements != null) {
            qeRequest.limit(maxElements);
        }
        if (!StringUtils.isBlank(filterExpression)){
            qeRequest.filterExpression(Expression.builder().expression(filterExpression).expressionValues(values).build());
        }
        if (StringUtils.isNotBlank(index)){
            return Flux.from(dynamoTable.index(index).query(qeRequest.build()).flatMapIterable(Page::items));
        }
        return Flux.from(dynamoTable.query(qeRequest.build()).flatMapIterable(Page::items));
    }

    public Flux<T> getByFilter(QueryConditional conditional, String index, Map<String, AttributeValue> values, String filterExpression){
        return getByFilter(conditional, index, values, filterExpression, null);
    }

    public Key keyBuild(String partitionKey, String sortKey){
        Key.Builder builder = Key.builder().partitionValue(partitionKey);
        if (StringUtils.isNotBlank(sortKey)){
            builder.sortValue(sortKey);
        }
        return builder.build();
    }

    public Flux<T> findAllByKeys(String partitionKey, String... sortKeys) {
        ReadBatch.Builder<T> builder = ReadBatch.builder(tClass)
                .mappedTableResource(this.dynamoTable);

        for(String sortKey: sortKeys ) {
            Key key = Key.builder().partitionValue(partitionKey).sortValue(sortKey).build();
            builder.addGetItem(key);
        }

        BatchGetResultPagePublisher batchGetResultPagePublisher = dynamoDbEnhancedAsyncClient.batchGetItem(BatchGetItemEnhancedRequest.builder()
                .readBatches(builder.build())
                .build());

        return Mono.from(batchGetResultPagePublisher.map(batchGetResultPage -> batchGetResultPage.resultsForTable(this.dynamoTable)))
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<Void> deleteBatch(String partitionKey, String... sortKeys) {
        WriteBatch.Builder<T> builder = WriteBatch.builder(tClass)
                .mappedTableResource(this.dynamoTable);

        for (String sortKey : sortKeys) {
            Key key = Key.builder().partitionValue(partitionKey).sortValue(sortKey).build();
            builder.addDeleteItem(key);
        }

        CompletableFuture<BatchWriteResult> batchWriteResultCompletableFuture = dynamoDbEnhancedAsyncClient.batchWriteItem(BatchWriteItemEnhancedRequest.builder()
                .addWriteBatch(builder.build())
                .build());


        return Mono.fromFuture(batchWriteResultCompletableFuture).then();
    }

    public Flux<T> batchGetItem(List<Tuple2<String, String>> keys) {
        return Flux.fromIterable(keys)
                .window(MAX_DYNAMODB_BATCH_SIZE)
                .flatMap(chunk -> {
                    ReadBatch.Builder<T> builder = ReadBatch.builder(tClass)
                            .mappedTableResource(dynamoTable);
                    Mono<BatchGetResultPage> deferred = Mono.defer(() ->
                            Mono.from(dynamoDbEnhancedAsyncClient.batchGetItem(BatchGetItemEnhancedRequest.builder()
                                    .readBatches(builder.build())
                                    .build())));
                    return chunk
                            .doOnNext(item -> {
                                Key key =keyBuild(item.getT1(), item.getT2());
                                builder.addGetItem(key);
                            })
                            .then(deferred);
                })
                .flatMap(page -> {
                    List<T> results = page.resultsForTable(dynamoTable);
                    log.debug("request size: {}, query result size: {}", keys.size(), results.size());
                    if (!page.unprocessedKeysForTable(dynamoTable).isEmpty()) {
                        List<Key> unprocessedKeys = page.unprocessedKeysForTable(dynamoTable);
                        List<Tuple2<String, String>> unprocessedEntities = filterItemAlreadyProcessed(keys, unprocessedKeys);
                        log.info("unprocessed entities {} over total entities {}", unprocessedEntities.size(), keys.size());
                        return Flux.fromIterable(results)
                                .concatWith(batchGetItem(unprocessedEntities));
                    }
                    return Flux.fromIterable(results);
                });
    }

    private List<Tuple2<String, String>> filterItemAlreadyProcessed(List<Tuple2<String, String>> keys, List<Key> unprocessedKeys) {
        Set<Key> setKeys = new HashSet<>(unprocessedKeys);
        return keys.stream()
                .filter(entity -> {
                    Key key =  keyBuild(entity.getT1(), entity.getT2());
                    return setKeys.contains(key);
                })
                .toList();
    }

}
