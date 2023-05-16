package it.pagopa.pn.commons.utils.dynamodb.async.sync;

import it.pagopa.pn.commons.utils.LogUtils;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.function.Consumer;

@EqualsAndHashCode
@Slf4j
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
        LogUtils.logPuttingDynamoDBEntity(dynamoDbTable.tableName(), request.item());
        this.dynamoDbTable.putItem(request);
    }

    @Override
    public void putItem(Consumer<PutItemEnhancedRequest.Builder<T>> requestConsumer) {
        this.dynamoDbTable.putItem(requestConsumer);
    }

    @Override
    public void putItem(T item) {
        LogUtils.logPuttingDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        this.dynamoDbTable.putItem(item);
    }

    @Override
    public T getItem(Key key) {
        T item = this.dynamoDbTable.getItem(key);
        LogUtils.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T getItem(T keyItem) {
        T item = this.dynamoDbTable.getItem(keyItem);
        LogUtils.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T getItem(Consumer<GetItemEnhancedRequest.Builder> requestConsumer) {
        T item = this.dynamoDbTable.getItem(requestConsumer);
        LogUtils.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T getItem(GetItemEnhancedRequest request) {
        T item = this.dynamoDbTable.getItem(request);
        LogUtils.logGetDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T deleteItem(Consumer<DeleteItemEnhancedRequest.Builder> requestConsumer) {
        T item = this.dynamoDbTable.deleteItem(requestConsumer);
        LogUtils.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T deleteItem(DeleteItemEnhancedRequest request) {
        T item = this.dynamoDbTable.deleteItem(request);
        LogUtils.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T deleteItem(Key key) {
        T item = this.dynamoDbTable.deleteItem(key);
        LogUtils.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T deleteItem(T keyItem) {
        T item = this.dynamoDbTable.deleteItem(keyItem);
        LogUtils.logDeleteDynamoDBEntity(this.dynamoDbTable.tableName(), item);
        return item;
    }

    @Override
    public T updateItem(T item) {
        T updateItem = this.dynamoDbTable.updateItem(item);
        LogUtils.logUpdateDynamoDBEntity(this.dynamoDbTable.tableName(), updateItem);
        return updateItem;
    }

    @Override
    public T updateItem(Consumer<UpdateItemEnhancedRequest.Builder<T>> requestConsumer) {
        T updateItem = this.dynamoDbTable.updateItem(requestConsumer);
        LogUtils.logUpdateDynamoDBEntity(this.dynamoDbTable.tableName(), updateItem);
        return updateItem;
    }

    @Override
    public T updateItem(UpdateItemEnhancedRequest<T> request) {
        T updateItem = this.dynamoDbTable.updateItem(request);
        LogUtils.logUpdateDynamoDBEntity(this.dynamoDbTable.tableName(), updateItem);
        return updateItem;
    }

}
