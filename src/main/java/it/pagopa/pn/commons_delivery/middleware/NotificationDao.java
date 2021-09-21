package it.pagopa.pn.commons_delivery.middleware;

import it.pagopa.pn.api.dto.NotificationSearchRow;
import it.pagopa.pn.api.dto.notification.Notification;
import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.commons.abstractions.IdConflictException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationDao {

    static final String IMPLEMENTATION_TYPE_PROPERTY_NAME = "pn.middleware.impl.delivery-dao";

    void addNotification(Notification notification ) throws IdConflictException;

    Optional<Notification> getNotificationByIun(String iun );

    List<NotificationSearchRow> searchSentNotification(
            String senderId, Instant startDate, Instant endDate,
            String recipientId, NotificationStatus status, String subjectRegExp
    );


}
