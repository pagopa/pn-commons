package it.pagopa.pn.commons.utils.dynamodb.sync;

import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.function.Consumer;

@EqualsAndHashCode
@CustomLog
public class DynamoDbEnhancedClientDecorator implements DynamoDbEnhancedClient {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public DynamoDbEnhancedClientDecorator(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    @Override
    public <T> DynamoDbTable<T> table(String s, TableSchema<T> tableSchema) {
        return new DynamoDbTableDecorator<>(this.dynamoDbEnhancedClient.table(s, tableSchema));
    }

    @Override
    public Void transactWriteItems(Consumer<TransactWriteItemsEnhancedRequest.Builder> requestConsumer) {
        return this.dynamoDbEnhancedClient.transactWriteItems(requestConsumer);
    }

    @Override
    public Void transactWriteItems(TransactWriteItemsEnhancedRequest request) {
        request.transactWriteItems().forEach(log::logTransactionDynamoDBEntity);
        return this.dynamoDbEnhancedClient.transactWriteItems(request);
    }

    @Override
    public BatchGetResultPageIterable batchGetItem(BatchGetItemEnhancedRequest request) {
        return this.dynamoDbEnhancedClient.batchGetItem(request);
    }

    @Override
    public BatchWriteResult batchWriteItem(BatchWriteItemEnhancedRequest request) {
        return dynamoDbEnhancedClient.batchWriteItem(request);
    }

    @Override
    public BatchWriteResult batchWriteItem(Consumer<BatchWriteItemEnhancedRequest.Builder> requestConsumer) {
        return dynamoDbEnhancedClient.batchWriteItem(requestConsumer);
    }

}
