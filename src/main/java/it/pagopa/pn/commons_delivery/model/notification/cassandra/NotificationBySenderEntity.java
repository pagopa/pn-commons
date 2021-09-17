package it.pagopa.pn.commons_delivery.model.notification.cassandra;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;

@Table("iun_by_senderid")
@Getter
@Builder
public class NotificationBySenderEntity {

    @PrimaryKey
    private NotificationBySenderEntityId senderId;

    private String paNotificationId;
    private Map<String,String> recipientsJson;
    private String subject;

}



