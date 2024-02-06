package it.pagopa.pn.commons.middleware.dynamo.dao;

import it.pagopa.pn.commons.db.BaseDAO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.util.Objects;


@Repository
@Profile("test-it")
public class TestIntegrationDAOActivator extends BaseDAO<TestIntegrationDAOActivator.FirstEntity> {


    protected TestIntegrationDAOActivator(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient, DynamoDbAsyncClient dynamoDbAsyncClient) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, "FirstTable", FirstEntity.class);
    }

    public Mono<FirstEntity> createEntity(FirstEntity entity){
        return super.put(entity);
    }

    public Mono<FirstEntity> updateEntity(FirstEntity entity) {
        return super.update(entity);
    }

    public Mono<FirstEntity> deleteEntity(String partitionKey, String sortKey){
        return super.delete(partitionKey, sortKey);
    }

    public Mono<Void> creteWithTransaction(FirstEntity firstEntity) {
        TransactPutItemEnhancedRequest<FirstEntity> requestEntity =
                TransactPutItemEnhancedRequest.builder(TestIntegrationDAOActivator.FirstEntity.class)
                        .item(firstEntity)
                        .build();

        TransactWriteItemsEnhancedRequest request = TransactWriteItemsEnhancedRequest.builder()
                .addPutItem(super.dynamoTable, requestEntity).build();
        return super.putWithTransact(request);
    }

    public Mono<FirstEntity> findBy(String partitionKey, String sortKey) {
        return super.get(partitionKey, sortKey);
    }

    @DynamoDbBean
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class FirstEntity {
        @Getter(onMethod = @__({@DynamoDbPartitionKey, @DynamoDbAttribute("firstId")}))
        private String firstId;

        @Getter(onMethod = @__({@DynamoDbSortKey,@DynamoDbAttribute("description")}))
        private String description;

        @Getter(onMethod = @__({@DynamoDbAttribute("price")}))
        private Double price;

        @Getter(onMethod = @__({@DynamoDbSecondaryPartitionKey(indexNames = "index-first"), @DynamoDbAttribute("name")}))
        private String name;

        @Getter(onMethod = @__({@DynamoDbSecondarySortKey(indexNames = "index-first"), @DynamoDbAttribute("code")}))
        private String code;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FirstEntity that = (FirstEntity) o;
            return Objects.equals(firstId, that.firstId) && Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstId, description);
        }
    }


}
