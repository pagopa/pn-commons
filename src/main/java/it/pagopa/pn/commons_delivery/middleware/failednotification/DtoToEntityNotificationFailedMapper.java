package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.api.dto.notification.failednotification.PaperNotificationFailed;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntityId;
import org.springframework.stereotype.Component;

@Component
public class DtoToEntityNotificationFailedMapper {

    public PaperNotificationFailedEntity dto2Entity(PaperNotificationFailed dto) {
        return PaperNotificationFailedEntity.builder().id(
                PaperNotificationFailedEntityId.builder()
                        .recipientId(dto.getRecipientId())
                        .iun(dto.getIun()).build()
        ).build();
    }
}
