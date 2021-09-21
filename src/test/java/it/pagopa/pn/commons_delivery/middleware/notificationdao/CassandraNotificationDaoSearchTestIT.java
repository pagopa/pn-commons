package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.NotificationSearchRow;
import it.pagopa.pn.api.dto.notification.Notification;
import it.pagopa.pn.api.dto.notification.NotificationAttachment;
import it.pagopa.pn.api.dto.notification.NotificationRecipient;
import it.pagopa.pn.api.dto.notification.NotificationSender;
import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.NotificationDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        NotificationDao.IMPLEMENTATION_TYPE_PROPERTY_NAME + "=" + MiddlewareTypes.CASSANDRA,
        "spring.data.cassandra.keyspace-name=pn_delivery_test",
        "spring.data.cassandra.cluster-name=cassandra",
        "spring.data.cassandra.local-datacenter=datacenter1",
        "spring.data.cassandra.contact-points=localhost",
        "spring.data.cassandra.username=cassandra",
        "spring.data.cassandra.password=cassandra",
        "spring.data.cassandra.schema-action=CREATE_IF_NOT_EXISTS"
})
@ContextConfiguration(classes = {
        CassandraNotificationDao.class,
        CassandraNotificationEntityDao.class,
        CassandraNotificationDaoSearchTestIT.TestContext.class,
        CassandraNotificationBySenderEntityDao.class,
        DtoToBySenderEntityMapper.class,
        DtoToEntityNotificationMapper.class,
        EntityToDtoNotificationMapper.class,
        CassandraAutoConfiguration.class,
        CassandraDataAutoConfiguration.class
})
@EntityScan(basePackages = {"it.pagopa.pn"})
class CassandraNotificationDaoSearchTestIT {

    @Autowired
    private CassandraNotificationDao dao;

    @Autowired
    private CassandraOperations cassandra;

    @Test
    void testSimple() throws IdConflictException {
        String senderId = "pa1";

        Notification n = Notification.builder()
                .iun(UUID.randomUUID().toString())
                .sentAt(Instant.EPOCH.plus(1, ChronoUnit.MINUTES))
                .sender(NotificationSender.builder().paId(senderId).build())
                .recipients(Collections.singletonList(
                                NotificationRecipient.builder()
                                        .taxId("recipientId")
                                        .build()
                        )
                )
                .documents(Collections.singletonList(
                        NotificationAttachment.builder()
                                .body("body")
                                .digests(NotificationAttachment.Digests.builder()
                                        .sha256("aaaa")
                                        .build())
                                .contentType("content/type")
                                .savedVersionId("v1")
                                .build()
                ))
                .build();

        dao.addNotification(n);

        List<NotificationSearchRow> result = dao.searchSentNotification(
                senderId,
                Instant.EPOCH,
                Instant.EPOCH.plus(1, ChronoUnit.MINUTES),
                null,
                null,
                null
        );

        Set<String> senderIds = result.stream()
                .map(row -> row.getSenderId())
                .collect(Collectors.toSet());
        Assertions.assertEquals(1, senderIds.size());
        Assertions.assertTrue(senderIds.contains(senderId));
    }

    @Test
    void statusTest() throws IdConflictException {
        String senderId = "pa1";

        Notification n = Notification.builder()
                .iun(UUID.randomUUID().toString())
                .sentAt(Instant.EPOCH.plus(1, ChronoUnit.MINUTES))
                .sender(NotificationSender.builder().paId(senderId).build())
                .recipients(Collections.singletonList(
                                NotificationRecipient.builder()
                                        .taxId("recipientId")
                                        .build()
                        )
                )
                .documents(Collections.singletonList(
                        NotificationAttachment.builder()
                                .body("body")
                                .digests(NotificationAttachment.Digests.builder()
                                        .sha256("aaaa")
                                        .build())
                                .contentType("content/type")
                                .savedVersionId("v1")
                                .build()
                ))
                .build();

        dao.addNotification(n);

        List<NotificationSearchRow> result = dao.searchSentNotification(
                senderId,
                Instant.EPOCH,
                Instant.EPOCH.plus(1, ChronoUnit.MINUTES),
                null,
                NotificationStatus.RECEIVED,
                null
        );

        Set<String> senderIds = result.stream()
                .map(row -> row.getSenderId())
                .collect(Collectors.toSet());
        Set<NotificationStatus> statuses = result.stream()
                .map(row -> row.getNotificationStatus())
                .collect(Collectors.toSet());
        Assertions.assertEquals(1, senderIds.size());
        Assertions.assertTrue(senderIds.contains(senderId));
        Assertions.assertTrue(statuses.contains(NotificationStatus.RECEIVED));

    }


    @Test
    void recipientTest() throws IdConflictException {
        String senderId = "pa1";
        String recipientId = "CodiceFiscale1";

        Notification n = Notification.builder()
                .iun(UUID.randomUUID().toString())
                .sentAt(Instant.EPOCH.plus(1, ChronoUnit.MINUTES))
                .sender(NotificationSender.builder().paId(senderId).build())
                .recipients(Collections.singletonList(
                                NotificationRecipient.builder()
                                        .taxId(recipientId)
                                        .build()
                        )
                )
                .documents(Collections.singletonList(
                        NotificationAttachment.builder()
                                .body("body")
                                .digests(NotificationAttachment.Digests.builder()
                                        .sha256("aaaa")
                                        .build())
                                .contentType("content/type")
                                .savedVersionId("v1")
                                .build()
                ))
                .build();

        dao.addNotification(n);

        List<NotificationSearchRow> result = dao.searchSentNotification(
                senderId,
                Instant.EPOCH,
                Instant.EPOCH.plus(1, ChronoUnit.MINUTES),
                recipientId,
                NotificationStatus.RECEIVED,
                null
        );

        Set<String> senderIds = result.stream()
                .map(row -> row.getSenderId())
                .collect(Collectors.toSet());
        Set<String> recipients = result.stream()
                .map(row -> row.getRecipientId())
                .collect(Collectors.toSet());

        Assertions.assertEquals(1, senderIds.size());
        Assertions.assertTrue(senderIds.contains(senderId));

        Assertions.assertEquals(1, recipients.size());
        Assertions.assertTrue(recipients.contains(recipientId));

    }

    @Test
    void subjectTest() throws IdConflictException {
        String senderId = "pa1";
        String subjectRegExp = ".*Test";

        Notification n = Notification.builder()
                .iun(UUID.randomUUID().toString())
                .sentAt(Instant.EPOCH.plus(1, ChronoUnit.MINUTES))
                .sender(NotificationSender.builder().paId(senderId).build())
                .recipients(Collections.singletonList(
                                NotificationRecipient.builder()
                                        .taxId("recipientId")
                                        .build()
                        )
                )
                .subject("Subject Test")
                .documents(Collections.singletonList(
                        NotificationAttachment.builder()
                                .body("body")
                                .digests(NotificationAttachment.Digests.builder()
                                        .sha256("aaaa")
                                        .build())
                                .contentType("content/type")
                                .savedVersionId("v1")
                                .build()
                ))
                .build();

        dao.addNotification(n);

        List<NotificationSearchRow> result = dao.searchSentNotification(
                senderId,
                Instant.EPOCH,
                Instant.EPOCH.plus(1, ChronoUnit.MINUTES),
                null,
                NotificationStatus.RECEIVED,
                subjectRegExp
        );



        Set<String> senderIds = result.stream()
                .map(row -> row.getSenderId())
                .collect(Collectors.toSet());
        Set<String> subjects = result.stream()
                .map(row -> row.getSubject())
                .collect(Collectors.toSet());

        Assertions.assertEquals(1, senderIds.size());
        Assertions.assertTrue(senderIds.contains(senderId));
        Assertions.assertTrue(subjects.stream().allMatch(s -> s.matches(subjectRegExp)));
    }


    @Configuration
    @EntityScan(basePackages = {"it.pagopa.pn"})
    public static class TestContext {

        @Bean
        public ObjectMapper objMapper() {
            return new ObjectMapper();
        }
    }
}
