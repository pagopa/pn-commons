package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import it.pagopa.pn.api.dto.NotificationSearchRow;
import it.pagopa.pn.api.dto.notification.Notification;
import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.NotificationDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationBySenderEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = NotificationDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
@Slf4j
public class CassandraNotificationDao implements NotificationDao {

    private final CassandraOperations cassandraTemplate;
    private final KeyValueStore<String, NotificationEntity> notificationEntityDao;
    private final KeyValueStore<String, NotificationBySenderEntity> notificationBySenderEntityDao;
    private final DtoToEntityNotificationMapper dto2entityMapper;
    private final DtoToBySenderEntityMapper dto2BySenderEntityMapper;
    private final EntityToDtoNotificationMapper entity2dtoMapper;

    public CassandraNotificationDao(
            CassandraOperations cassandraTemplate, KeyValueStore<String, NotificationEntity> notificationEntityDao,
            KeyValueStore<String, NotificationBySenderEntity> notificationBySenderEntityDao,
            DtoToEntityNotificationMapper dto2entityMapper,
            DtoToBySenderEntityMapper dto2BySenderEntityMapper,
            EntityToDtoNotificationMapper entity2dtoMapper) {
        this.cassandraTemplate = cassandraTemplate;
        this.notificationEntityDao = notificationEntityDao;
        this.notificationBySenderEntityDao = notificationBySenderEntityDao;
        this.dto2entityMapper = dto2entityMapper;
        this.dto2BySenderEntityMapper = dto2BySenderEntityMapper;
        this.entity2dtoMapper = entity2dtoMapper;
    }

    @Override
    public void addNotification(Notification notification) throws IdConflictException {
        List<NotificationBySenderEntity> bySenderEntity = dto2BySenderEntityMapper.dto2Entity(notification, NotificationStatus.RECEIVED);
        bySenderEntity.forEach(entity ->
                notificationBySenderEntityDao.put(entity));
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
        Predicate<String> matchSubject = buildRegexpPredicate( subjectRegExp );

        List<NotificationSearchRow> result;
        if( status != null ) {
            result = executeSentNotificationQuery( senderId, startDate, endDate, recipientId, status, subjectRegExp);
        }
        else {
            result = new ArrayList<>();
            for( NotificationStatus oneStatus: NotificationStatus.values() ) {
                List<NotificationSearchRow> oneStatusResult = executeSentNotificationQuery(
                               senderId, startDate, endDate, recipientId, oneStatus, subjectRegExp);
                result.addAll( oneStatusResult );
            }
        }

        return result.stream()
                .filter( row -> matchSubject.test( row.getSubject() ))
                .sorted(Comparator.comparing( NotificationSearchRow::getSentAt ))
                .collect(Collectors.toList());
    }

    private Predicate<String> buildRegexpPredicate(String subjectRegExp) {
        Predicate<String> matchSubject;
        if( subjectRegExp != null ) {
            matchSubject = Pattern.compile(subjectRegExp).asMatchPredicate();
        }
        else {
            matchSubject = x -> true;
        }
        return matchSubject;
    }

    private List<NotificationSearchRow> executeSentNotificationQuery(
            String senderId, Instant startDate, Instant endDate,
            String recipientId, NotificationStatus status, String subjectRegExp
    ) {
        Query query = generateSearchSentNotificationQuery(
                senderId, startDate, endDate, recipientId, status, subjectRegExp );

        return cassandraTemplate.select(query, NotificationBySenderEntity.class)
                .stream().map(entity -> NotificationSearchRow.builder()
                        .iun(entity.getSenderId().getIun())
                        .sentAt(entity.getSenderId().getSentat())
                        .senderId(entity.getSenderId().getSenderId())
                        .notificationStatus(entity.getSenderId().getNotificationStatus())
                        .recipientId(entity.getSenderId().getRecipientId())
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
        Query query = Query.query(
                Criteria.where("senderId.notificationStatus").is( status ),
                Criteria.where("senderId.senderId").is( senderId ),
                Criteria.where("senderId.sentat").gte( startDate ),
                Criteria.where("senderId.sentat").lte( endDate )
            );
        if(StringUtils.isNotBlank( recipientId )) {
            query = query.and( Criteria.where("recipientId").is( recipientId) );
        }
        return query;
    }

}
