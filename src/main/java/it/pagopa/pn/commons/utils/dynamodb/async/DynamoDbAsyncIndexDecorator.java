package it.pagopa.pn.commons.utils.dynamodb.async;


import it.pagopa.pn.commons.utils.MDCUtils;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClientExtension;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Map;
import java.util.function.Consumer;

@EqualsAndHashCode
public class DynamoDbAsyncIndexDecorator<T> implements DynamoDbAsyncIndex<T> {

    private final DynamoDbAsyncIndex<T> tDynamoDbAsyncIndex;

    public DynamoDbAsyncIndexDecorator(DynamoDbAsyncIndex<T> tDynamoDbAsyncIndex) {
        this.tDynamoDbAsyncIndex = tDynamoDbAsyncIndex;
    }

    @Override
    public DynamoDbEnhancedClientExtension mapperExtension() {
        return this.tDynamoDbAsyncIndex.mapperExtension();
    }

    @Override
    public TableSchema<T> tableSchema() {
        return this.tDynamoDbAsyncIndex.tableSchema();
    }

    @Override
    public String tableName() {
        return this.tDynamoDbAsyncIndex.tableName();
    }

    @Override
    public String indexName() {
        return this.tDynamoDbAsyncIndex.indexName();
    }

    @Override
    public Key keyFrom(T item) {
        return this.tDynamoDbAsyncIndex.keyFrom(item);
    }

    @Override
    public SdkPublisher<Page<T>> query(QueryConditional queryConditional) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.tDynamoDbAsyncIndex.query(queryConditional)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
    }

    @Override
    public SdkPublisher<Page<T>> query(QueryEnhancedRequest request) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.tDynamoDbAsyncIndex.query(request)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
    }

    @Override
    public SdkPublisher<Page<T>> query(Consumer<QueryEnhancedRequest.Builder> requestConsumer) {
        Map<String, String> copyOfContextMap = MDCUtils.retrieveMDCContextMap();
        return this.tDynamoDbAsyncIndex.query(requestConsumer)
                .map(tPage -> MDCUtils.enrichWithMDC(tPage, copyOfContextMap));
    }
}
