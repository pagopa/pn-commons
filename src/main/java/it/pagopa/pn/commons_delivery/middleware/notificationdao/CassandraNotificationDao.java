package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import it.pagopa.pn.api.dto.NotificationSearchRow;
import it.pagopa.pn.api.dto.notification.Notification;
import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.NotificationDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.QueryOptions;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = NotificationDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
@Slf4j
public class CassandraNotificationDao implements NotificationDao {

    private final CassandraOperations cassandraTemplate;
    private final KeyValueStore<String, NotificationEntity> notificationEntityDao;
    private final KeyValueStore<NotificationBySenderEntityId, NotificationBySenderEntity> notificationBySenderEntityDao;
    private final KeyValueStore<NotificationByRecipientEntityId, NotificationByRecipientEntity> notificationByRecipientEntityDao;
    private final DtoToEntityNotificationMapper dto2entityMapper;
    private final DtoToBySenderEntityMapper dto2BySenderEntityMapper;
    private final DtoToByRecipientEntityMapper dto2ByRecipientEntityMapper;
    private final EntityToDtoNotificationMapper entity2dtoMapper;

    public CassandraNotificationDao(
            CassandraOperations cassandraTemplate,
            KeyValueStore<String, NotificationEntity> notificationEntityDao,
            KeyValueStore<NotificationBySenderEntityId, NotificationBySenderEntity> notificationBySenderEntityDao,
            KeyValueStore<NotificationByRecipientEntityId, NotificationByRecipientEntity> notificationByRecipientEntityDao,
            DtoToEntityNotificationMapper dto2entityMapper,
            DtoToBySenderEntityMapper dto2BySenderEntityMapper,
            DtoToByRecipientEntityMapper dto2ByRecipientEntityMapper,
            EntityToDtoNotificationMapper entity2dtoMapper) {
        this.cassandraTemplate = cassandraTemplate;
        this.notificationEntityDao = notificationEntityDao;
        this.notificationBySenderEntityDao = notificationBySenderEntityDao;
        this.notificationByRecipientEntityDao = notificationByRecipientEntityDao;
        this.dto2entityMapper = dto2entityMapper;
        this.dto2BySenderEntityMapper = dto2BySenderEntityMapper;
        this.dto2ByRecipientEntityMapper = dto2ByRecipientEntityMapper;
        this.entity2dtoMapper = entity2dtoMapper;
    }

    @Override
    public void addNotification(Notification notification) throws IdConflictException {
        List<NotificationBySenderEntity> bySenderEntity = dto2BySenderEntityMapper.dto2Entity(notification, NotificationStatus.RECEIVED);
        bySenderEntity.forEach(entity ->
                notificationBySenderEntityDao.put(entity));
        List<NotificationByRecipientEntity> byRecipientEntity = dto2ByRecipientEntityMapper.dto2Entity(notification, NotificationStatus.RECEIVED);
        byRecipientEntity.forEach(entity ->
                notificationByRecipientEntityDao.put(entity));
        NotificationEntity entity = dto2entityMapper.dto2Entity(notification);
        notificationEntityDao.putIfAbsent(entity);
    }

    @Override
    public Optional<Notification> getNotificationByIun(String iun) {
        return notificationEntityDao.get(iun)
                .map(entity2dtoMapper::entity2Dto);
    }

    @Override
    public List<NotificationSearchRow> searchSentNotification(
            String senderId, Instant startDate, Instant endDate,
            String recipientId, NotificationStatus status, String subjectRegExp
    ) {
        Predicate<String> matchSubject = buildRegexpPredicate(subjectRegExp);
        Predicate<String> matchRecipient = buildRecipientIdPredicate(recipientId);

        List<NotificationSearchRow> result;
        if (status != null) {
            result = executeSentNotificationQuery(senderId, startDate, endDate, recipientId, status, subjectRegExp);
        } else {
            result = new ArrayList<>();
            for (NotificationStatus oneStatus : NotificationStatus.values()) {
                List<NotificationSearchRow> oneStatusResult = executeSentNotificationQuery(
                        senderId, startDate, endDate, recipientId, oneStatus, subjectRegExp);
                result.addAll(oneStatusResult);
            }
        }

        return result.stream()
                .filter(row -> matchRecipient.test(row.getRecipientId()))
                .filter(row -> matchSubject.test(row.getSubject()))
                .sorted(Comparator.comparing(NotificationSearchRow::getSentAt))
                .collect(Collectors.toList());

    }

    @Override
    public List<NotificationSearchRow> searchReceivedNotification(
            String recipientId, Instant startDate,
            Instant endDate, String senderId,
            NotificationStatus status, String subjectRegExp) {

        Predicate<String> matchSubject = buildRegexpPredicate(subjectRegExp);

        List<NotificationSearchRow> result;
        if (status != null) {
            result = executeReceivedNotificationQuery(recipientId, startDate, endDate, senderId, status, subjectRegExp);
        } else {
            result = new ArrayList<>();
            for (NotificationStatus oneStatus : NotificationStatus.values()) {
                List<NotificationSearchRow> oneStatusResult = executeReceivedNotificationQuery(
                        recipientId, startDate, endDate, senderId, oneStatus, subjectRegExp);
                result.addAll(oneStatusResult);
            }
        }

        return result.stream()
                .filter(row -> matchSubject.test(row.getSubject()))
                .sorted(Comparator.comparing(NotificationSearchRow::getSentAt))
                .collect(Collectors.toList());
    }


    Predicate<String> buildRegexpPredicate(String subjectRegExp) {
        Predicate<String> matchSubject;
        if (subjectRegExp != null) {
            matchSubject = Objects::nonNull;
            matchSubject = matchSubject.and(Pattern.compile("^" + subjectRegExp + "$").asMatchPredicate());
        } else {
            matchSubject = x -> true;
        }
        return matchSubject;
    }


    private Predicate<String> buildRecipientIdPredicate(String recipientId) {
        Predicate<String> matchSubject;
        if (recipientId != null) {
            matchSubject = s -> recipientId.equals(s);
        } else {
            matchSubject = x -> true;
        }
        return matchSubject;
    }

    private List<NotificationSearchRow> executeSentNotificationQuery(
            String senderId, Instant startDate, Instant endDate,
            String recipientId, NotificationStatus status, String subjectRegExp
    ) {
        Query query = generateSearchSentNotificationQuery(
                senderId, startDate, endDate, recipientId, status, subjectRegExp);

        return cassandraTemplate.select(query, NotificationBySenderEntity.class)
                .stream().map(entity -> NotificationSearchRow.builder()
                        .iun(entity.getNotificationBySenderId().getIun())
                        .sentAt(entity.getNotificationBySenderId().getSentat())
                        .senderId(entity.getNotificationBySenderId().getSenderId())
                        .notificationStatus(entity.getNotificationBySenderId().getNotificationStatus())
                        .recipientId(entity.getNotificationBySenderId().getRecipientId())
                        .paNotificationId(entity.getPaNotificationId())
                        .subject(entity.getSubject())
                        .build()
                )
                .collect(Collectors.toList());
    }
    //TODO accorpare metodi cambia solo metodo generazione query
    private List<NotificationSearchRow> executeReceivedNotificationQuery(
            String recipientId, Instant startDate,
            Instant endDate, String senderId,
            NotificationStatus status, String subjectRegExp) {
        Query query = generateSearchReceivedNotificationQuery(
                recipientId, startDate, endDate, senderId, status, subjectRegExp);

        return cassandraTemplate.select(query, NotificationByRecipientEntity.class)
                .stream().map(entity -> NotificationSearchRow.builder()
                        .iun(entity.getNotificationByRecipientId().getIun())
                        .sentAt(entity.getNotificationByRecipientId().getSentat())
                        .senderId(entity.getNotificationByRecipientId().getSenderId())
                        .notificationStatus(entity.getNotificationByRecipientId().getNotificationStatus())
                        .recipientId(entity.getNotificationByRecipientId().getRecipientId())
                        .paNotificationId(entity.getPaNotificationId())
                        .subject(entity.getSubject())
                        .build()
                )
                .collect(Collectors.toList());

    }



    private Query generateSearchSentNotificationQuery(
            String senderId, Instant startDate, Instant endDate, String recipientId,
            NotificationStatus status, String subjectRegExp
    ) {
        return Query.query(
                Criteria.where("notificationBySenderId.notificationStatus").is(status),
                Criteria.where("notificationBySenderId.senderId").is(senderId),
                Criteria.where("notificationBySenderId.sentat").gte(startDate),
                Criteria.where("notificationBySenderId.sentat").lte(endDate)
        ).queryOptions(QueryOptions.builder()
                .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
                .build()
        );
    }

    private Query generateSearchReceivedNotificationQuery(
            String recipientId, Instant startDate,
            Instant endDate, String senderId,
            NotificationStatus status, String subjectRegExp) {

        return Query.query(
                Criteria.where("notificationByRecipientId.notificationStatus").is(status),
                Criteria.where("notificationByRecipientId.recipientId").is(recipientId),
                Criteria.where("notificationByRecipientId.sentat").gte(startDate),
                Criteria.where("notificationByRecipientId.sentat").lte(endDate)
        ).queryOptions(QueryOptions.builder()
                .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
                .build()
        );
    }

}
