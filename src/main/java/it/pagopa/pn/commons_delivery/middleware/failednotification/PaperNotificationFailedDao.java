package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.api.dto.notification.failednotification.PaperNotificationFailed;

import java.util.Set;

public interface PaperNotificationFailedDao {
    static final String IMPLEMENTATION_TYPE_PROPERTY_NAME = "pn.middleware.impl.failed-notification";

    void addPaperNotificationFailed(PaperNotificationFailed paperNotificationFailed);

    Set<PaperNotificationFailed> getNotificationByRecipientId(String recipientId);

    void deleteNotificationFailed(String recipientId, String iun);

}
