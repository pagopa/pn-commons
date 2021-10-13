package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

class CassandraNotificationDaoTest extends AbstractNotificationDaoTest {

    private EntityToDtoNotificationMapper entity2dto;
    private CassandraNotificationDao specificDao;

    @BeforeEach
    void instantiateDao() {
        ObjectMapper objMapper = new ObjectMapper();
        DtoToEntityNotificationMapper dto2Entity = new DtoToEntityNotificationMapper(objMapper);
        entity2dto = new EntityToDtoNotificationMapper(objMapper);
        KeyValueStore<String, NotificationEntity> entityDao = new EntityDaoMock();
        KeyValueStore<NotificationBySenderEntityId, NotificationBySenderEntity> notificationBySenderEntityDao = Mockito.mock(KeyValueStore.class);
        KeyValueStore<NotificationByRecipientEntityId, NotificationByRecipientEntity> notificationByRecipientEntityDao = Mockito.mock(KeyValueStore.class);
        DtoToSearchEntityMapper dto2SearchEntityMapper = Mockito.mock(DtoToSearchEntityMapper.class);
        specificDao = new CassandraNotificationDao(null, entityDao, notificationBySenderEntityDao, notificationByRecipientEntityDao, dto2Entity, dto2SearchEntityMapper, entity2dto);
        dao = specificDao;
    }

    @Override
    @Test
    void insertSuccessWithoutPayments() throws IdConflictException {
        super.insertSuccessWithoutPayments();
    }

    @Override
    @Test
    void insertSuccessWithPaymentsDeliveryMode() throws IdConflictException {
        super.insertSuccessWithPaymentsDeliveryMode();
    }

    @Override
    @Test
    void insertSuccessWithPaymentsFlat() throws IdConflictException {
        super.insertSuccessWithPaymentsFlat();
    }

    @Override
    @Test
    void insertSuccessWithPaymentsIuvOnly() throws IdConflictException {
        super.insertSuccessWithPaymentsIuvOnly();
    }

    @Override
    @Test
    void insertSuccessWithPaymentsNoIuv() throws IdConflictException {
        super.insertSuccessWithPaymentsNoIuv();
    }

    @Override
    @Test
    void insertFailForIunConflict() throws IdConflictException {
        super.insertFailForIunConflict();
    }

    @Test
    void testWrongRecipientJson() {
        // GIVEN
        String cf = "CodiceFiscale";
        NotificationEntity entity = NotificationEntity.builder()
                .recipientsJson(Collections.singletonMap(cf, "WRONG JSON"))
                .recipientsOrder(Collections.singletonList(cf))
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto(entity);

        // THEN
        Assertions.assertThrows(PnInternalException.class, todo);
    }

    @Test
    void testWrongDocumentsLength() {
        // GIVEN
        NotificationEntity entity = NotificationEntity.builder()
                .documentsVersionIds(Arrays.asList("v1", "v2"))
                .documentsDigestsSha256(Collections.singletonList("doc1"))
                .recipientsJson(Collections.emptyMap())
                .recipientsOrder(Collections.emptyList())
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto(entity);

        // THEN
        Assertions.assertThrows(PnInternalException.class, todo);
    }

    @Test
    void testWrongF24Metadata1() {
        // GIVEN
        NotificationEntity entity = NotificationEntity.builder()
                .documentsVersionIds(Collections.emptyList())
                .documentsDigestsSha256(Collections.emptyList())
                .recipientsJson(Collections.emptyMap())
                .recipientsOrder(Collections.emptyList())
                .f24DigitalVersionId(null)
                .f24DigitalDigestSha256("sha256")
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto(entity);

        // THEN
        Assertions.assertThrows(PnInternalException.class, todo);
    }

    @Test
    void testWrongF24Metadata2() {
        // GIVEN
        NotificationEntity entity = NotificationEntity.builder()
                .documentsVersionIds(Collections.emptyList())
                .documentsDigestsSha256(Collections.emptyList())
                .recipientsJson(Collections.emptyMap())
                .recipientsOrder(Collections.emptyList())
                .f24DigitalVersionId("version")
                .f24DigitalDigestSha256(null)
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto(entity);

        // THEN
        Assertions.assertThrows(PnInternalException.class, todo);
    }

    @Test
    void regExpMatchTest() {

        Predicate<String> predicate = this.specificDao.buildRegexpPredicate("Test");
        //boolean b = Pattern.compile("^Test$").matcher("Subject Test").matches();

        Assertions.assertTrue(predicate.test("Test"));
        Assertions.assertFalse(predicate.test("Subject Test"));

        Predicate<String> predicate2 = this.specificDao.buildRegexpPredicate(".*Test");

        Assertions.assertTrue(predicate2.test("Test"));
        Assertions.assertTrue(predicate2.test("Subject Test"));

    }


    private static class EntityDaoMock implements KeyValueStore<String, NotificationEntity> {

        private final Map<String, NotificationEntity> storage = new ConcurrentHashMap<>();

        @Override
        public void put(NotificationEntity notificationEntity) {
            storage.put(notificationEntity.getIun(), notificationEntity);
        }

        @Override
        public void putIfAbsent(NotificationEntity notificationEntity) throws IdConflictException {
            NotificationEntity previous = storage.putIfAbsent(notificationEntity.getIun(), notificationEntity);
            if (previous != null) {
                throw new IdConflictException(notificationEntity.getIun());
            }
        }

        @Override
        public Optional<NotificationEntity> get(String iun) {
            NotificationEntity entity = storage.get(iun);
            return Optional.ofNullable(entity);
        }

        @Override
        public void delete(String iun) {
            storage.remove(iun);
        }
    }


}
