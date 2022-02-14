package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.notification.status.NotificationStatusHistoryElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons_delivery.middleware.TimelineDao;
import it.pagopa.pn.commons_delivery.middleware.notificationdao.CassandraNotificationByRecipientEntityDao;
import it.pagopa.pn.commons_delivery.middleware.notificationdao.CassandraNotificationBySenderEntityDao;
import it.pagopa.pn.commons_delivery.middleware.notificationdao.CassandraNotificationEntityDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.*;
import it.pagopa.pn.commons_delivery.utils.StatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = TimelineDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
@Slf4j
public class CassandraTimelineDao implements TimelineDao {

    private final TimelineEntityDao entityDao;
    private final CassandraNotificationEntityDao notificationEntityDao;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;
    private final CassandraNotificationBySenderEntityDao notificationBySenderEntityDao;
    private final CassandraNotificationByRecipientEntityDao notificationByRecipientEntityDao;
    private final StatusUtils statusUtils;

    public CassandraTimelineDao(
            TimelineEntityDao entityDao,
            CassandraNotificationEntityDao notificationEntityDao,
            CassandraNotificationBySenderEntityDao notificationBySenderEntityDao,
            CassandraNotificationByRecipientEntityDao notificationByRecipientEntityDao,
            DtoToEntityTimelineMapper dto2entity,
            EntityToDtoTimelineMapper entity2dto,
            StatusUtils statusUtils) {
        this.entityDao = entityDao;
        this.notificationEntityDao = notificationEntityDao;
        this.notificationBySenderEntityDao = notificationBySenderEntityDao;
        this.notificationByRecipientEntityDao = notificationByRecipientEntityDao;
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
        this.statusUtils = statusUtils;
    }

    @Override
    public Optional<TimelineElement> getTimelineElement(String iun, String timelineId) {
        TimelineElementEntityId id = TimelineElementEntityId.builder()
                .iun(iun)
                .timelineElementId(timelineId)
                .build();
        return entityDao.get(id)
                .map(entity2dto::entityToDto);
    }

    @Override
    public Set<TimelineElement> getTimeline(String iun) {
        return entityDao.findByIun(iun)
                .stream()
                .map(entity2dto::entityToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteTimeline(String iun) {
        entityDao.deleteByIun(iun);
    }

    @Override
    public void addTimelineElement(TimelineElement dto) {
        String iun = dto.getIun();

        // - Caricare i metadati della notifica utilizzando CassandraNotificationEntityDao
        Optional<NotificationEntity> notificationEntityOptional = notificationEntityDao.get(iun);
        if (notificationEntityOptional.isPresent()) {

            NotificationEntity notificationEntity = notificationEntityOptional.get();

            // - Caricare la timeline corrente utilizzando il metodo getTimeline
            Set<TimelineElement> currentTimeline = this.getTimeline(iun);

            // - Calcolare lo stato corrente
            NotificationBySenderEntity currentSearchBySenderEntry = computeSearchBySenderEntry(notificationEntity, currentTimeline);
            NotificationStatus currentState = currentSearchBySenderEntry.getNotificationBySenderId().getNotificationStatus();
            NotificationByRecipientEntity currentSearchByRecipientEntry = computeSearchByRecipientEntry(notificationEntity, currentTimeline);

            // - aggiungere all'elenco della timeline il nuovo dto
            currentTimeline.add(dto);

            // - Calcolare il nuovo stato
            NotificationBySenderEntity nextSearchBySenderEntry = computeSearchBySenderEntry(notificationEntity, currentTimeline);
            NotificationStatus nextState = nextSearchBySenderEntry.getNotificationBySenderId().getNotificationStatus();
            NotificationByRecipientEntity nextSearchByRecipientEntry = computeSearchByRecipientEntry(notificationEntity, currentTimeline);


            // - se i due stati differiscono
            if (!currentState.equals(nextState)) {
                log.warn(" CAMBIAMENTO DI STATO " + currentState + " " + nextState);
                addNewSearchEntries(nextSearchBySenderEntry, nextSearchByRecipientEntry, notificationEntity);
                deleteOldSearchEntries(currentSearchBySenderEntry, currentSearchByRecipientEntry, notificationEntity);
            }
        } else {
            throw new PnInternalException("Try to add timeline element for non existing iun " + dto.getIun());
        }

        TimelineElementEntity entity = dto2entity.dtoToEntity(dto);
        entityDao.put(entity);
    }


    private void deleteOldSearchEntries(NotificationBySenderEntity nextSearchBySenderEntry, NotificationByRecipientEntity nextSearchByRecipientEntry, NotificationEntity notificationEntity) {

        for (String recipientId : notificationEntity.getRecipientsOrder()) {
            notificationBySenderEntityDao.delete(
                    nextSearchBySenderEntry.getNotificationBySenderId().toBuilder()
                            .recipientId(recipientId)
                            .build()
            );
            notificationByRecipientEntityDao.delete(
                    nextSearchByRecipientEntry.getNotificationByRecipientId().toBuilder()
                            .recipientId(recipientId)
                            .build()
            );
        }

    }

    private void addNewSearchEntries(NotificationBySenderEntity currentSearchBySenderEntry, NotificationByRecipientEntity currentSearchByRecipientEntry, NotificationEntity notificationEntity) {

        for (String recipientId : notificationEntity.getRecipientsOrder()) {
            notificationBySenderEntityDao.put(currentSearchBySenderEntry.toBuilder()
                    .notificationBySenderId(currentSearchBySenderEntry.getNotificationBySenderId()
                            .toBuilder()
                            .recipientId(recipientId)
                            .build()
                    )
                    .build());
            notificationByRecipientEntityDao.put(currentSearchByRecipientEntry.toBuilder()
                    .notificationByRecipientId(currentSearchByRecipientEntry.getNotificationByRecipientId()
                            .toBuilder()
                            .recipientId(recipientId)
                            .build()
                    )
                    .build());
        }

    }

    private NotificationBySenderEntity computeSearchBySenderEntry(NotificationEntity notificationEntity, Set<TimelineElement> currentTimeline) {
        int numberOfRecipient = notificationEntity.getRecipientsOrder().size();
        Instant notificationCreatedAt = notificationEntity.getSentAt();

        List<NotificationStatusHistoryElement> historyElementList = statusUtils.getStatusHistory(
                currentTimeline,
                numberOfRecipient,
                notificationCreatedAt);

        NotificationStatusHistoryElement lastStatus;
        lastStatus = historyElementList.get(historyElementList.size() - 1);

        return NotificationBySenderEntity.builder()
                .notificationBySenderId(NotificationBySenderEntityId.builder()
                        .notificationStatus(lastStatus.getStatus())
                        .senderId(notificationEntity.getSenderPaId())
                        .sentat(notificationCreatedAt)
                        .recipientId(null)
                        .iun(notificationEntity.getIun())
                        .build()
                )
                .paNotificationId(notificationEntity.getPaNotificationId())
                .recipientsJson(notificationEntity.getRecipientsJson())
                .subject(notificationEntity.getSubject())
                .build();
    }

    private NotificationByRecipientEntity computeSearchByRecipientEntry(NotificationEntity notificationEntity, Set<TimelineElement> currentTimeline) {
        int numberOfRecipient = notificationEntity.getRecipientsOrder().size();
        Instant notificationCreatedAt = notificationEntity.getSentAt();

        List<NotificationStatusHistoryElement> historyElementList = statusUtils.getStatusHistory(
                currentTimeline,
                numberOfRecipient,
                notificationCreatedAt);

        NotificationStatusHistoryElement lastStatus;
        lastStatus = historyElementList.get(historyElementList.size() - 1);

        return NotificationByRecipientEntity.builder()
                .paNotificationId(notificationEntity.getPaNotificationId())
                .recipientsJson(notificationEntity.getRecipientsJson())
                .subject(notificationEntity.getSubject())
                .notificationByRecipientId(
                        NotificationByRecipientEntityId.builder()
                                .notificationStatus(lastStatus.getStatus())
                                .senderId(notificationEntity.getSenderPaId())
                                .sentat(notificationCreatedAt)
                                .iun(notificationEntity.getIun())
                                .build()
                )
                .build();
    }
}
