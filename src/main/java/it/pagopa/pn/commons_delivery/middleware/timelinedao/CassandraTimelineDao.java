package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.TimelineDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntityId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty( name = TimelineDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA )
public class CassandraTimelineDao implements TimelineDao {

    private final TimelineEntityDao entityDao;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;

    public CassandraTimelineDao(
            TimelineEntityDao entityDao,
            DtoToEntityTimelineMapper dto2entity,
            EntityToDtoTimelineMapper entity2dto ) {
        this.entityDao = entityDao;
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
    }

    @Override
    public void addTimelineElement( TimelineElement dto ) {
        TimelineElementEntity entity = dto2entity.dtoToEntity( dto );
        entityDao.put( entity );
    }

    @Override
    public Optional<TimelineElement> getTimelineElement(String iun, String timelineId) {
        TimelineElementEntityId id = TimelineElementEntityId.builder()
                .iun( iun )
                .timelineElementId( timelineId )
                .build();
        return entityDao.get( id )
                .map( entity2dto::entityToDto );
    }

    @Override
    public Set<TimelineElement> getTimeline(String iun) {
        return entityDao.listByIun( iun )
                .stream()
                .map( entity2dto::entityToDto )
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteTimeline(String iun) {
        entityDao.deleteByIun( iun );
    }
}
