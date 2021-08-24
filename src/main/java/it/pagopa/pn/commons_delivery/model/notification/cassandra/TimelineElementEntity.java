package it.pagopa.pn.commons_delivery.model.notification.cassandra;


import it.pagopa.pn.api.dto.notification.timeline.TimelineElementCategory;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table( TimelineElementEntity.TIMELINE_TABLE_NAME )
@Getter
@Builder
public class TimelineElementEntity {

    public static final String TIMELINE_TABLE_NAME = "timelines";

    @PrimaryKey
    private TimelineElementEntityId id;

    private Instant timestamp;
    private TimelineElementCategory category;
    private String details;

}

