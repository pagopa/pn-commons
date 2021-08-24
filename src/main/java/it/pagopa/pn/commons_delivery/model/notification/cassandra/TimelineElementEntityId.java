package it.pagopa.pn.commons_delivery.model.notification.cassandra;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;


@Builder
@Getter
@PrimaryKeyClass
@EqualsAndHashCode
public class TimelineElementEntityId implements Serializable {

    @PrimaryKeyColumn( name = "iun", ordinal = 0, type = PrimaryKeyType.PARTITIONED )
    private String iun;

    @PrimaryKeyColumn( name = "timeline_element_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING )
    private String timelineElementId;

}
