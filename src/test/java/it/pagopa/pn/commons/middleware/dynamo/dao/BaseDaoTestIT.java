package it.pagopa.pn.commons.middleware.dynamo.dao;

import it.pagopa.pn.commons.LocalStackTestConfig;
import it.pagopa.pn.commons.abstractions.impl.AbstractCachedSsmParameterConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test-it")
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = { "pn.commons.features.is-mvp-default-value=true",
        "pn.env.runtime=DEVELOPMENT"})
@Import(LocalStackTestConfig.class)
class BaseDaoTestIT {
    @SpyBean
    @Autowired
    private TestIngrationDAOActivator testIngrationDAOActivator;
    @MockBean
    private AbstractCachedSsmParameterConsumer abstractCachedSsmParameterConsumer;
    private TestIngrationDAOActivator.FirstEntity firstEntity;

    @BeforeEach
    void setUp() {
        firstEntity = new TestIngrationDAOActivator.FirstEntity();
        firstEntity.setFirstId("1rf3og4r32");
        firstEntity.setDescription("description");
    }

    @Test
    void whenPutEntityThenReturnEntitySaved() {
        Mono<TestIngrationDAOActivator.FirstEntity> result = testIngrationDAOActivator.createEntity(firstEntity);
        StepVerifier.create(result)
                .expectNextMatches(firstEntity::equals)
                .verifyComplete();

    }

    @Test
    void whenEntityIsNullThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> {
            testIngrationDAOActivator.createEntity(null).block();
        });
    }

    @Test
    void  whenDeleteEntityThenReturnEntityDeleted() {
        firstEntity.setFirstId("123");

        Mono<TestIngrationDAOActivator.FirstEntity> result = testIngrationDAOActivator.createEntity(firstEntity)
                .flatMap(saved -> this.testIngrationDAOActivator
                        .deleteEntity(this.firstEntity.getFirstId(), this.firstEntity.getDescription())
                );

        StepVerifier.create(result)
                .expectNextMatches(firstEntity::equals)
                .verifyComplete();
    }

    @Test
    void  whenDeleteEntityThatNotExistThenReturnEntityDeleted() {
        firstEntity.setFirstId("NotExisted");

        TestIngrationDAOActivator.FirstEntity result =
                this.testIngrationDAOActivator.deleteEntity(this.firstEntity.getFirstId(), this.firstEntity.getDescription()).block();

        assertNull(result);

    }

    @Test
    void whenPutWithTransactFindElementThatSaved() {
        firstEntity.setFirstId("transact");

        testIngrationDAOActivator.creteWithTransaction(firstEntity).block();
        TestIngrationDAOActivator.FirstEntity result = testIngrationDAOActivator.findBy(firstEntity.getFirstId(), firstEntity.getDescription()).block();

        assertNotNull(result);
        assertEquals(firstEntity, result);

    }

    @Test
    void whenUpdateTest() {
        firstEntity.setFirstId("UpdateEntity");
        Mono<TestIngrationDAOActivator.FirstEntity> result = testIngrationDAOActivator.createEntity(firstEntity)
                .map(saved -> {
                    saved.setDescription("test-update");
                    return saved;
                })
                .flatMap(testIngrationDAOActivator::updateEntity);

        StepVerifier.create(result)
                .expectNextMatches(entity -> entity.getDescription().equals("test-update"))
                .verifyComplete();

    }

    @Test
    void whenGetEntityNotExistedThenReturnNull() {
        TestIngrationDAOActivator.FirstEntity result = this.testIngrationDAOActivator.findBy("not-existed", "not-sorting").block();

        assertNull(result);
    }

    @Test
    void whenGetEntityExistedThenReturnEntity() {
        firstEntity.setFirstId("find-ok-test");

        Mono<TestIngrationDAOActivator.FirstEntity> result = testIngrationDAOActivator.createEntity(firstEntity)
                .flatMap(saved -> this.testIngrationDAOActivator.findBy(saved.getFirstId(), saved.getDescription()));

        StepVerifier.create(result)
                .expectNextMatches(firstEntity::equals)
                .verifyComplete();
    }

    @Test
    void whenGetBySecondaryIndexWithoutSecondaryIndexTest() {
        assertThrows(IllegalArgumentException.class,
                ()-> this.testIngrationDAOActivator
                .getBySecondaryIndex("No-secondary", "test-no-secondary", null)
                        .blockFirst()
        );
    }

    @Test
    void whenGetBySecondaryIndexWithSecondaryIndexTest() {
        Flux< TestIngrationDAOActivator.FirstEntity> result = this.testIngrationDAOActivator
                        .getBySecondaryIndex("index-first", "Name 1", null);

        StepVerifier.create(result)
                .expectNextCount(5)
                .verifyComplete();

        result = this.testIngrationDAOActivator
                .getBySecondaryIndex("index-first", "Name 1", "Code 1");

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void whenGetByFilterTest() {
        TestIngrationDAOActivator.FirstEntity test = new TestIngrationDAOActivator.FirstEntity();
        test.setFirstId("id1");
        test.setDescription("Descrizione 1");

        QueryConditional conditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(test.getFirstId()).build()
        );



        Flux<TestIngrationDAOActivator.FirstEntity> result = this.testIngrationDAOActivator.getByFilter(conditional, null, null, null, 100);

        StepVerifier.create(result)
                .expectNext(test)
                .verifyComplete();
    }

    @Test
    void whenGetByFilterWithSecondaryIndexTest() {

        QueryConditional conditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue("Name 1").build()
        );

        Flux<TestIngrationDAOActivator.FirstEntity> result = this.testIngrationDAOActivator.getByFilter(conditional, "index-first", null, null, 100);

        StepVerifier.create(result)
                .expectNextCount(5)
                .verifyComplete();

        QueryConditional conditional1 = QueryConditional.keyEqualTo(
                Key.builder().partitionValue("Name 1").sortValue("Code 1").build()
        );

        result = this.testIngrationDAOActivator.getByFilter(conditional1, "index-first", null, null, 100);

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        Map<String, AttributeValue> map = new HashMap<>();
        map.put(":value", AttributeValue.builder().n("30.00").build());

        result = this.testIngrationDAOActivator.getByFilter(
                conditional, "index-first", map, "price > :value", 100);

        StepVerifier.create(result)
                .expectNextCount(3)
                .verifyComplete();
    }


    @Test
    void whenKeyBuildTest() {
        Key key = this.testIngrationDAOActivator.keyBuild("Abb", null);
        assertNotNull(key);
        assertEquals("Abb", key.partitionKeyValue().s());
        assertFalse(key.sortKeyValue().isPresent());

        key = this.testIngrationDAOActivator.keyBuild("Abb", "sort");
        assertNotNull(key);
        assertEquals("Abb", key.partitionKeyValue().s());
        assertTrue(key.sortKeyValue().isPresent());
        assertEquals("sort", key.sortKeyValue().get().s());
    }

    @Test
    void whenFindAllByKeysTest() {

        Flux<TestIngrationDAOActivator.FirstEntity> result =
                this.testIngrationDAOActivator.findAllByKeys("idDelete","Descrizione 4", "Descrizione 5", "Descrizione 6");

        StepVerifier.create(result)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void whenDeleteBatchTest() {
        this.testIngrationDAOActivator.deleteBatch("idDelete", "Descrizione 1", "Descrizione 2", "Descrizione 3").block();
        List<Tuple2<String, String>> keys = List.of(
                Tuples.of("idDelete", "Descrizione 1"),
                Tuples.of("idDelete", "Descrizione 2"),
                Tuples.of("idDelete", "Descrizione 3")
        );

        Flux<TestIngrationDAOActivator.FirstEntity> result = this.testIngrationDAOActivator.batchGetItem(keys);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void whenBatchGetItemTest() {
        List<Tuple2<String, String>> keys = createTestKeys(3, "id", "Descrizione ");
        Flux< TestIngrationDAOActivator.FirstEntity> result = this.testIngrationDAOActivator.batchGetItem(keys);

        StepVerifier.create(result)
                .expectNextCount(3)
                .verifyComplete();
    }

    private List<Tuple2<String, String>> createTestKeys(int size, String id, String sort) {
        List<Tuple2<String, String>> keys = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            keys.add(Tuples.of(id+i, sort+i));

        }
        return keys;
    }


}