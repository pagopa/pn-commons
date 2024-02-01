package it.pagopa.pn.commons.middleware.dynamo.dao;

import it.pagopa.pn.commons.LocalStackTestConfig;
import it.pagopa.pn.commons.db.BaseDAO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(LocalStackTestConfig.class)
class BaseDaoTest {
    @MockBean
    BaseDaoInstance baseDaoInstance;
    @MockBean
    DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @MockBean
    DynamoDbAsyncClient dynamoDbAsyncClient;
    @MockBean
    DynamoDbAsyncTable dynamoTable;


    @Test
    void whenPutEntityTest() {
        FirstEntity firstEntity = new FirstEntity();
        firstEntity.setFirstId("1rf3og4r32");
        firstEntity.setDescription("description");

    }

    @Test
    void whenDeleteEntityTest() {

    }

    @Test
    void whenPutWithTransactTest() {

    }

    @Test
    void whenUpdateTest() {

    }

    @Test
    void whenGetTest() {

    }

    @Test
    void whenGetBySecondaryIndexTest() {

    }

    @Test
    void whenGetByFilterTest() {

    }

    @Test
    void whenKeyBuildTest() {

    }

    @Test
    void whenFindAllByKeysTest() {

    }

    @Test
    void whenDeleteBatchTest() {

    }

    @Test
    void whenBatchGetItemTest() {

    }

    @Test
    void whenFilterMandateAlreadyProcessedTest() {

    }


    @DynamoDbBean
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    static class FirstEntity {
        @Getter(onMethod = @__({@DynamoDbPartitionKey,@DynamoDbAttribute("firstId")}))
        private String firstId;

        @Getter(onMethod = @__({@DynamoDbAttribute("description")}))
        private String description;
    }

    @DynamoDbBean
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    static class SecondEntity {
        @Getter(onMethod = @__({@DynamoDbPartitionKey,@DynamoDbAttribute("secondId")}))
        private String secondId;

        @Getter(onMethod = @__({@DynamoDbAttribute("value")}))
        private String value;

        @Getter(onMethod = @__({@DynamoDbSecondaryPartitionKey(indexNames = "index"), @DynamoDbAttribute("value")}))
        private String index;
    }

    @Repository
    private static class BaseDaoInstance extends BaseDAO{
        protected BaseDaoInstance(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient, DynamoDbAsyncClient dynamoDbAsyncClient, String tableName) {
            super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, tableName, FirstEntity.class);
        }
    }
}