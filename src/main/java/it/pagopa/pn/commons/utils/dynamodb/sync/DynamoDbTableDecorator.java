package it.pagopa.pn.commons.utils.dynamodb.sync;

import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.function.Consumer;

@EqualsAndHashCode
@CustomLog
public class DynamoDbTableDecorator<T> implements DynamoDbTable<T> {

    private final DynamoDbTable<T> dynamoDbTable;

    public DynamoDbTableDecorator(DynamoDbTable<T> dynamoDbTable) {
        this.dynamoDbTable = dynamoDbTable;
    }

    @Override
    public DynamoDbIndex<T> index(String indexName) {
        return dynamoDbTable.index(indexName);
    }


    @Override
    public DynamoDbEnhancedClientExtension mapperExtension() {
        return this.dynamoDbTable.mapperExtension();
    }

    @Override
    public TableSchema<T> tableSchema() {
        return this.dynamoDbTable.tableSchema();
    }

    @Override
    public String tableName() {
        return this.dynamoDbTable.tableName();
    }

    @Override
    public Key keyFrom(T t) {
        return this.dynamoDbTable.keyFrom(t);
    }

    @Override
    public PageIterable<T> query(QueryEnhancedRequest request) {
        return this.dynamoDbTable.query(request);
    }

    @Override
    public PageIterable<T> query(Consumer<QueryEnhancedRequest.Builder> requestConsumer) {
        return this.dynamoDbTable.query(requestConsumer);
    }

    @Override
    public PageIterable<T> query(QueryConditional queryConditional) {
        return this.dynamoDbTable.query(queryConditional);
    }

    @Override
    public void putItem(PutItemEnhancedRequest<T> request) {
        log.logPuttingDynamoDBEntity(dynamoDbTable.tableName(), request.item());
        this.dynamoDbTable.putItem(request);
        log.logPutDoneDynamoDBEntity(this.dynamoDbTable.tableName());
    }

    @Override
    public void putItem(Consumer<PutItemEnhancedRequest.Builder<T>> requestConsumer) {
        this.dynamoDbTable.putItem(requestConsumer);
        log.logPutDoneDynamoDBEntity(this.dynamoDbTable.tableName());
    }

    @Override
    public void putItem(T item) {
        log.logPuttingDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        this.dynamoDbTable.putItem(item);
        log.logPutDoneDynamoDBEntity(this.dynamoDbTable.tableName());
    }

    @Override
    public T getItem(Key key) {
        T item = this.dynamoDbTable.getItem(key);
        log.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), key, item);
        return item;
    }

    @Override
    public T getItem(T keyItem) {
        T item = this.dynamoDbTable.getItem(keyItem);
        log.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), keyItem, item);
        return item;
    }

    @Override
    public T getItem(Consumer<GetItemEnhancedRequest.Builder> requestConsumer) {
        return this.dynamoDbTable.getItem(requestConsumer);
    }

    @Override
    public T getItem(GetItemEnhancedRequest request) {
        T item = this.dynamoDbTable.getItem(request);
        log.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), request.key(), item);
        return item;
    }

    @Override
    public T deleteItem(Consumer<DeleteItemEnhancedRequest.Builder> requestConsumer) {
        return this.dynamoDbTable.deleteItem(requestConsumer);
    }

    @Override
    public T deleteItem(DeleteItemEnhancedRequest request) {
        T item = this.dynamoDbTable.deleteItem(request);
        log.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), request.key(), item);
        return item;
    }

    @Override
    public T deleteItem(Key key) {
        T item = this.dynamoDbTable.deleteItem(key);
        log.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), key, item);
        return item;
    }

    @Override
    public T deleteItem(T keyItem) {
        T item = this.dynamoDbTable.deleteItem(keyItem);
        log.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), keyItem, item);
        return item;
    }

    @Override
    public T updateItem(T item) {
        T updateItem = this.dynamoDbTable.updateItem(item);
        log.logUpdateDynamoDBEntity(this.dynamoDbTable.tableName(), updateItem);
        return updateItem;
    }

    @Override
    public T updateItem(Consumer<UpdateItemEnhancedRequest.Builder<T>> requestConsumer) {
        T updateItem = this.dynamoDbTable.updateItem(requestConsumer);
        log.logUpdateDynamoDBEntity(this.dynamoDbTable.tableName(), updateItem);
        return updateItem;
    }

    @Override
    public T updateItem(UpdateItemEnhancedRequest<T> request) {
        T updateItem = this.dynamoDbTable.updateItem(request);
        log.logUpdateDynamoDBEntity(this.dynamoDbTable.tableName(), updateItem);
        return updateItem;
    }

}
