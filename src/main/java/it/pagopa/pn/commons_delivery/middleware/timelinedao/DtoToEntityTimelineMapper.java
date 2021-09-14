package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementDetails;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntityId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DtoToEntityTimelineMapper {

    private final ObjectMapper objectMapper;
    private final Map<TimelineElementCategory, ObjectWriter> objectWriters;

    public DtoToEntityTimelineMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectWriters = new ConcurrentHashMap<>();
    }

    public TimelineElementEntity dtoToEntity(TimelineElement dto) {
        return TimelineElementEntity.builder()
                .id( TimelineElementEntityId.builder()
                        .iun( dto.getIun() )
                        .timelineElementId( dto.getElementId() )
                        .build()
                )
                .category( dto.getCategory() )
                .timestamp( dto.getTimestamp() )
                .details( detailsToJsonString( dto ) )
                .build();
    }


    private String detailsToJsonString( TimelineElement dto) {
        try {
            TimelineElementCategory category = dto.getCategory();
            ObjectWriter objWriter = getObjectWriter( category );
            TimelineElementDetails details = dto.getDetails();
            return objWriter.writeValueAsString( details );

        } catch (JsonProcessingException exc) {
            throw new PnInternalException( "Writing timeline detail to storage", exc );
        }
    }

    private ObjectWriter getObjectWriter( TimelineElementCategory timelineElementCategory ) {
        return this.objectWriters.computeIfAbsent(
                timelineElementCategory,
                // - generate reader of needed: objectWriter is thread safe, objectMapper isn't
                category -> {
                    synchronized ( this.objectMapper ) {
                        return this.objectMapper.writerFor( category.getDetailsJavaClass() );
                    }
                }
            );
    }

}
