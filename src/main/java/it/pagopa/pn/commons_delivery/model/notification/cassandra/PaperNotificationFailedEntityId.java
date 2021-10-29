package it.pagopa.pn.commons_delivery.model.notification.cassandra;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Builder(toBuilder = true)
@Getter
@PrimaryKeyClass
@EqualsAndHashCode
public class PaperNotificationFailedEntityId {

    @PrimaryKeyColumn(name = "recipientid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String recipientId;

    @PrimaryKeyColumn(name = "iun", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String iun;

}
