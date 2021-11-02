package it.pagopa.pn.commons_delivery.model.notification.cassandra;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("paper_notification_failed")
@Getter
@Builder
public class PaperNotificationFailedEntity {

    @PrimaryKey
    private PaperNotificationFailedEntityId id;

}
