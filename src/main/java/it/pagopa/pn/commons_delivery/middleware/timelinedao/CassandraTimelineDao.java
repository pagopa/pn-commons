package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineInfoDto;
import it.pagopa.pn.api.dto.status.RequestUpdateStatusDto;
import it.pagopa.pn.api.dto.status.ResponseUpdateStatusDto;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.TimelineDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntityId;
import it.pagopa.pn.commons_delivery.utils.PnDeliveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = TimelineDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
@Slf4j
public class CassandraTimelineDao implements TimelineDao {

    private final TimelineEntityDao entityDao;
    private final DtoToEntityTimelineMapper dto2entity;
    private final EntityToDtoTimelineMapper entity2dto;
    private final PnDeliveryClient client;
    
    public CassandraTimelineDao(
            TimelineEntityDao entityDao,
            DtoToEntityTimelineMapper dto2entity,
            EntityToDtoTimelineMapper entity2dto,
            PnDeliveryClient client) {
        this.entityDao = entityDao;
        this.dto2entity = dto2entity;
        this.entity2dto = entity2dto;
        this.client = client;
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
        Set<TimelineElement> currentTimeline = this.getTimeline(dto.getIun());

        RequestUpdateStatusDto requestDto = getRequestUpdateStatusDto(dto, currentTimeline);

        ResponseEntity<ResponseUpdateStatusDto> resp = client.updateState(requestDto);

        if (resp.getStatusCode().is2xxSuccessful()) {
            TimelineElementEntity entity = dto2entity.dtoToEntity(dto);
            entityDao.put(entity);
        }else {
            log.error("Status not updated correctly - iun {} timelineId {}", dto.getIun() , dto.getElementId());
        }
    }

    private RequestUpdateStatusDto getRequestUpdateStatusDto(TimelineElement dto, Set<TimelineElement> currentTimeline) {
        Set<TimelineInfoDto> currentTimelineInfoDto = currentTimeline.stream().map(elem ->
                TimelineInfoDto.builder()
                        .category(elem.getCategory())
                        .timestamp(elem.getTimestamp())
                        .build()
        ).collect(Collectors.toSet());

        return RequestUpdateStatusDto.builder()
                .iun(dto.getIun())
                .newTimelineElement(TimelineInfoDto.builder()
                        .timestamp(dto.getTimestamp())
                        .category(dto.getCategory())
                        .build())
                .currentTimeline(currentTimelineInfoDto)
                .build();
    }

}
