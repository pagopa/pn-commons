package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntityId;

import java.util.Set;

public interface PaperNotificationFailedEntityDao extends KeyValueStore<PaperNotificationFailedEntityId, PaperNotificationFailedEntity> {
    Set<PaperNotificationFailedEntity> findByRecipientId(String recipientId);
}
