package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.commons.abstractions.impl.AbstractCassandraKeyValueStore;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntityId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = PaperNotificationFailedDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
public class CassandraPaperNotificationFailedEntityDao extends AbstractCassandraKeyValueStore<PaperNotificationFailedEntityId, PaperNotificationFailedEntity>
        implements PaperNotificationFailedEntityDao {

    public static final String RECIPIENT_ID_COL = "recipientid";
    public static final String IUN_COL = "iun";

    private final CassandraOperations cassandraTemplate;

    public CassandraPaperNotificationFailedEntityDao(CassandraOperations cassandraTemplate) {
        super(cassandraTemplate, PaperNotificationFailedEntity.class);
        this.cassandraTemplate = cassandraTemplate;
    }

    @Override
    public Set<PaperNotificationFailedEntity> findByRecipientId(String recipientId) {
        return new HashSet<>(cassandraTemplate.select(queryByRecipientId(recipientId), PaperNotificationFailedEntity.class));
    }

    private Query queryByRecipientId(String recipientId) {
        return Query.query(Criteria.where(RECIPIENT_ID_COL).is(recipientId));
    }

}
