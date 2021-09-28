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
    private final DtoToSearchEntityMapper dto2SearchEntityMapper;
    private final EntityToDtoNotificationMapper entity2dtoMapper;

    public CassandraNotificationDao(
            CassandraOperations cassandraTemplate,
            KeyValueStore<String, NotificationEntity> notificationEntityDao,
            KeyValueStore<NotificationBySenderEntityId, NotificationBySenderEntity> notificationBySenderEntityDao,
            KeyValueStore<NotificationByRecipientEntityId, NotificationByRecipientEntity> notificationByRecipientEntityDao,
            DtoToEntityNotificationMapper dto2entityMapper,
            DtoToSearchEntityMapper dto2SearchEntityMapper,
            EntityToDtoNotificationMapper entity2dtoMapper) {
        this.cassandraTemplate = cassandraTemplate;
        this.notificationEntityDao = notificationEntityDao;
        this.notificationBySenderEntityDao = notificationBySenderEntityDao;
        this.notificationByRecipientEntityDao = notificationByRecipientEntityDao;
        this.dto2entityMapper = dto2entityMapper;
        this.dto2SearchEntityMapper = dto2SearchEntityMapper;
        this.entity2dtoMapper = entity2dtoMapper;
    }

    @Override
    public void addNotification(Notification notification) throws IdConflictException {
        List<NotificationBySenderEntity> bySenderEntity = dto2SearchEntityMapper.dto2SenderEntity(notification, NotificationStatus.RECEIVED);
        bySenderEntity.forEach(entity ->
                notificationBySenderEntityDao.put(entity));
        List<NotificationByRecipientEntity> byRecipientEntity = dto2SearchEntityMapper.dto2RecipientEntity(notification, NotificationStatus.RECEIVED);
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
    public List<NotificationSearchRow> searchNotification(
            boolean bySender, String senderReceiverId, Instant startDate, Instant endDate,
            String filterId, NotificationStatus status, String subjectRegExp
    ) {
        Predicate<String> matchSubject = buildRegexpPredicate(subjectRegExp);
        Predicate<String> matchFilter = buildFilterIdPredicate(filterId);

        List<NotificationSearchRow> result;
        if (status != null) {
            result = executeSearchNotificationQuery(bySender, senderReceiverId, startDate, endDate, status);
        } else {
            result = new ArrayList<>();
            for (NotificationStatus oneStatus : NotificationStatus.values()) {
                List<NotificationSearchRow> oneStatusResult = executeSearchNotificationQuery(
                        bySender, senderReceiverId, startDate, endDate, oneStatus);
                result.addAll(oneStatusResult);
            }
        }

        return result.stream()
                .filter(row -> matchFilter.test(bySender ? row.getRecipientId() : row.getSenderId()))
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


    private Predicate<String> buildFilterIdPredicate(String filterId) {
        Predicate<String> matchSubject;
        if (filterId != null) {
            matchSubject = s -> filterId.equals(s);
        } else {
            matchSubject = x -> true;
        }
        return matchSubject;
    }

    private List<NotificationSearchRow> executeSearchNotificationQuery(
            boolean bySender, String senderReceiverId, Instant startDate, Instant endDate,
            NotificationStatus status
    ) {
        Query query = generateSearchNotificationQuery(bySender, senderReceiverId, startDate, endDate, status);

        if (bySender) {
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
        } else {
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
    }

    private Query generateSearchNotificationQuery(
            boolean bySender, String senderReceiverId, Instant startDate, Instant endDate,
            NotificationStatus status
    ) {
        String entityIdProperty = bySender ? "notificationBySenderId" : "notificationByRecipientId";
        String senderReceiverProperty = entityIdProperty + "." + (bySender ? "senderId" : "recipientId");
        return Query.query(
                Criteria.where(entityIdProperty + ".notificationStatus").is(status),
                Criteria.where(senderReceiverProperty).is(senderReceiverId),
                Criteria.where(entityIdProperty + ".sentat").gte(startDate),
                Criteria.where(entityIdProperty + ".sentat").lte(endDate)
        ).queryOptions(QueryOptions.builder()
                .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
                .build()
        );
    }
}
