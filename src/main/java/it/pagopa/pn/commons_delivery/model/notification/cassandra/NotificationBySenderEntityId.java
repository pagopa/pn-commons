package it.pagopa.pn.commons_delivery.model.notification.cassandra;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

@Builder
@Getter
@PrimaryKeyClass
@EqualsAndHashCode
public class NotificationBySenderEntityId implements Serializable {
    @PrimaryKeyColumn(name = "notificationstatus", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private NotificationStatus notificationStatus;

    @PrimaryKeyColumn(name = "senderid", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String senderId;

    @PrimaryKeyColumn(name = "sentat", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private Instant sentat;

    @PrimaryKeyColumn(name = "recipientid", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String recipientId;

    @PrimaryKeyColumn(name = "iun", ordinal = 4, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String iun;

}
