package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class CassandraNotificationDaoTest extends AbstractNotificationDaoTest {

    private EntityToDtoNotificationMapper entity2dto;

    @BeforeEach
    void instantiateDao() {
        ObjectMapper objMapper = new ObjectMapper();
        DtoToEntityNotificationMapper dto2Entity = new DtoToEntityNotificationMapper( objMapper );
        entity2dto = new EntityToDtoNotificationMapper( objMapper );

        KeyValueStore<String, NotificationEntity> entityDao = new EntityDaoMock();
        dao = new CassandraNotificationDao( entityDao, dto2Entity , entity2dto );
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
                .recipientsJson( Collections.singletonMap( cf, "WRONG JSON"))
                .recipientsOrder(Collections.singletonList( cf ))
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto( entity );

        // THEN
        Assertions.assertThrows( PnInternalException.class,  todo );
    }

    @Test
    void testWrongDocumentsLength() {
        // GIVEN
        NotificationEntity entity = NotificationEntity.builder()
                .documentsVersionIds(Arrays.asList("v1", "v2"))
                .documentsDigestsSha256(Collections.singletonList( "doc1" ))
                .recipientsJson( Collections.emptyMap() )
                .recipientsOrder( Collections.emptyList() )
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto( entity );

        // THEN
        Assertions.assertThrows( PnInternalException.class,  todo );
    }

    @Test
    void testWrongF24Metadata1() {
        // GIVEN
        NotificationEntity entity = NotificationEntity.builder()
                .documentsVersionIds( Collections.emptyList() )
                .documentsDigestsSha256( Collections.emptyList() )
                .recipientsJson( Collections.emptyMap() )
                .recipientsOrder( Collections.emptyList() )
                .f24DigitalVersionId( null )
                .f24DigitalDigestSha256( "sha256" )
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto( entity );

        // THEN
        Assertions.assertThrows( PnInternalException.class,  todo );
    }

    @Test
    void testWrongF24Metadata2() {
        // GIVEN
        NotificationEntity entity = NotificationEntity.builder()
                .documentsVersionIds( Collections.emptyList() )
                .documentsDigestsSha256( Collections.emptyList() )
                .recipientsJson( Collections.emptyMap() )
                .recipientsOrder( Collections.emptyList() )
                .f24DigitalVersionId( "version" )
                .f24DigitalDigestSha256( null )
                .build();

        // WHEN
        Executable todo = () -> entity2dto.entity2Dto( entity );

        // THEN
        Assertions.assertThrows( PnInternalException.class,  todo );
    }


    private static class EntityDaoMock implements KeyValueStore<String, NotificationEntity> {

        private final Map<String, NotificationEntity> storage = new ConcurrentHashMap<>();

        @Override
        public void put(NotificationEntity notificationEntity) {
            storage.put( notificationEntity.getIun(), notificationEntity );
        }

        @Override
        public void putIfAbsent(NotificationEntity notificationEntity) throws IdConflictException {
            NotificationEntity previous = storage.putIfAbsent(notificationEntity.getIun(), notificationEntity);
            if( previous != null ) {
                throw new IdConflictException( notificationEntity.getIun() );
            }
        }

        @Override
        public Optional<NotificationEntity> get(String iun) {
            NotificationEntity entity = storage.get( iun );
            return Optional.ofNullable( entity );
        }

        @Override
        public void delete(String iun) {
            storage.remove( iun );
        }
    }


}
