package it.pagopa.pn.commons_delivery.middleware.notificationdao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.pagopa.pn.api.dto.notification.Notification;
import it.pagopa.pn.api.dto.notification.NotificationRecipient;
import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationBySenderEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.NotificationBySenderEntityId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DtoToBySenderEntityMapper {

    private final ObjectWriter objectWriter;

    public DtoToBySenderEntityMapper(ObjectMapper objMapper) {
        this.objectWriter = objMapper.writerFor(NotificationRecipient.class);
    }

    public List<NotificationBySenderEntity> dto2Entity(Notification dto, NotificationStatus status) {
        NotificationBySenderEntity.NotificationBySenderEntityBuilder builder = NotificationBySenderEntity.builder()
                .paNotificationId(dto.getPaNotificationId())
                .recipientsJson(recipientList2json(dto.getRecipients()))
                .subject(dto.getSubject());
        NotificationBySenderEntityId.NotificationBySenderEntityIdBuilder builderId = NotificationBySenderEntityId.builder()
                .senderId(dto.getSender().getPaId())
                .notificationStatus(status)
                .iun(dto.getIun())
                .sentat(dto.getSentAt());

        return dto.getRecipients().stream()
                .map(recipient ->
                        builder
                                .senderId(builderId
                                        .recipientId(recipient.getTaxId())
                                        .build())
                                .build())
                .toList();

    }

    private Map<String, String> recipientList2json(List<NotificationRecipient> recipients) {
        Map<String, String> result = new ConcurrentHashMap<>();
        recipients.forEach(recipient ->
                result.put(recipient.getTaxId(), recipient2JsonString(recipient))
        );
        return result;
    }

    private String recipient2JsonString(NotificationRecipient recipient) {
        try {
            return objectWriter.writeValueAsString(recipient);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException(exc);
        }
    }
}
