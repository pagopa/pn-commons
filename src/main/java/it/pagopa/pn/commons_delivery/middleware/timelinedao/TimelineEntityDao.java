package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntityId;

import java.util.List;

public interface TimelineEntityDao extends KeyValueStore<TimelineElementEntityId, TimelineElementEntity> {

    List<TimelineElementEntity> listByIun(String iun );

    void deleteByIun(String iun);
}
