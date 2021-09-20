package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.notification.status.NotificationStatusHistoryElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.TimelineDao;
import it.pagopa.pn.commons_delivery.middleware.notificationdao.CassandraNotificationBySenderEntityDao;
import it.pagopa.pn.commons_delivery.middleware.notificationdao.CassandraNotificationEntityDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.*;
import it.pagopa.pn.commons_delivery.utils.StatusUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = TimelineDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
public class CassandraTimelineDao implements TimelineDao {

    private final TimelineEntityDao entityDao;
    private final CassandraNotificationEntityDao notificationEntityDao;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;
    private final CassandraNotificationBySenderEntityDao notificationBySenderEntityDao;

    public CassandraTimelineDao(
            TimelineEntityDao entityDao,
            CassandraNotificationEntityDao notificationEntityDao,
            CassandraNotificationBySenderEntityDao notificationBySenderEntityDao,
            DtoToEntityTimelineMapper dto2entity,
            EntityToDtoTimelineMapper entity2dto) {
        this.entityDao = entityDao;
        this.notificationEntityDao = notificationEntityDao;
        this.notificationBySenderEntityDao = notificationBySenderEntityDao;
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
    }

    @Override
    public void addTimelineElement(TimelineElement dto) {
        // FIXME: PER LA GESTIONE DEL CAMBIO DI STATO
        // - Caricare i metadati della notifica utilizzando CassandraNotificationEntityDao
        Optional<NotificationEntity> notificationEntity = notificationEntityDao.get(dto.getIun());
        if (notificationEntity.isPresent()) {

            // - Caricare la timeline corrente utilizzando il metodo getTimeline
            // - Ordinarla temporalemnte
            Set<TimelineElement> currentTimeline = this.getTimeline(
                            notificationEntity.get()
                                    .getIun());


            // - Calcolare lo stato corrente
            NotificationStatus currentState = computeState(currentTimeline, notificationEntity.get());

            // - aggiungere all'elenco della timeline il nuovo dto
            currentTimeline.add(dto);

            // - Calcolare il nuovo stato
            NotificationStatus nextState = computeState(currentTimeline, notificationEntity.get());

            // - se i due stati differiscono
            if (!currentState.equals(nextState)) {
                NotificationBySenderEntity entityToDelete = NotificationBySenderEntity.builder()
                        .notificationBySenderId(NotificationBySenderEntityId.builder()
                                .iun(notificationEntity.get().getIun())
                                .senderId(notificationEntity.get().getSenderPaId())
                                .sentat(notificationEntity.get().getSentAt())
                                .notificationStatus(currentState)
                                .build())
                        .subject(notificationEntity.get().getSubject())
                        .paNotificationId(notificationEntity.get().getPaNotificationId())
                        .build();

                NotificationBySenderEntity newEntity = NotificationBySenderEntity.builder()
                        .notificationBySenderId(
                                NotificationBySenderEntityId.builder()
                                        .senderId(notificationEntity.get().getSenderPaId())
                                        .sentat(Instant.now())
                                        .iun(notificationEntity.get().getIun())
                                        .build()
                        )
                        .build();
                //   - utilizzare CassandraNotificationBySenderEntityDao per rimovere la entry con il vecchio stato
                //notificationBySenderEntityDao.delete(entityToDelete);

                //   - utilizzare CassandraNotificationBySenderEntityDao per inserire la entry con il nuovo stato
                notificationBySenderEntityDao.put(newEntity);
            }
        }

        TimelineElementEntity entity = dto2entity.dtoToEntity(dto);
        entityDao.put(entity);
    }

    private NotificationStatus computeState(Set<TimelineElement> currentTimeline, NotificationEntity notificationEntity) {
        StatusUtils utils = new StatusUtils();

        int numberOfRecipient = notificationEntity.getRecipientsJson().size();
        Instant notificationTimestamp = notificationEntity.getSentAt();
        List<NotificationStatusHistoryElement> historyElementList = utils.getStatusHistory(
                currentTimeline,
                numberOfRecipient,
                notificationTimestamp);
        return utils.getCurrentStatus(historyElementList);
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
}
