package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.pagopa.pn.api.dto.legalfacts.LegalFactsListEntryId;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElement;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;
import it.pagopa.pn.api.dto.notification.timeline.TimelineElementDetails;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EntityToDtoTimelineMapper {

    private final ObjectMapper objectMapper;
    private final Map<TimelineElementCategory, ObjectReader> objectReaders;

    public EntityToDtoTimelineMapper(ObjectMapper objectMapper ) {
        this.objectMapper = objectMapper;
        this.objectReaders = new ConcurrentHashMap<>();
    }

    public TimelineElement entityToDto( TimelineElementEntity entity ) {
        return TimelineElement.builder()
                .iun( entity.getId().getIun() )
                .elementId( entity.getId().getTimelineElementId() )
                .category( entity.getCategory() )
                .timestamp( entity.getTimestamp() )
                .details( parseDetailsFromJson( entity ))
                .legalFactsIds( parseLegalFactIdsFromJson( entity ) )
                .build();
    }

    private List<LegalFactsListEntryId> parseLegalFactIdsFromJson(TimelineElementEntity entity) {
        try {
            LegalFactsListEntryId[] legalFactsListEntryIds;
            legalFactsListEntryIds = objectMapper.readValue( entity.getLegalFactId(), LegalFactsListEntryId[].class );
            return legalFactsListEntryIds == null ? null : Arrays.asList( legalFactsListEntryIds );
        } catch (JsonProcessingException exc) {
            throw new PnInternalException( "Reading timeline detail from storage", exc );
        }
    }


    private TimelineElementDetails parseDetailsFromJson( TimelineElementEntity entity) {
        try {

            TimelineElementCategory category = entity.getCategory();
            ObjectReader objectReader = getObjectReader( category );
            return objectReader.readValue( entity.getDetails() );

        } catch (JsonProcessingException exc) {
            throw new PnInternalException( "Reading timeline detail from storage", exc );
        }
    }

    private ObjectReader getObjectReader( TimelineElementCategory timelineElementCategory ) {
        return this.objectReaders.computeIfAbsent(
                timelineElementCategory,
                // - generate reader of needed: objectReader is thread safe, object mapper don't
                category -> {
                    synchronized ( this.objectMapper ) {
                        return this.objectMapper.readerFor( category.getDetailsJavaClass() );
                    }
                }
            );
    }

}
