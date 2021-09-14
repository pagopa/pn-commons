package it.pagopa.pn.commons_delivery.middleware.timelinedao;

import it.pagopa.pn.commons.abstractions.impl.AbstractCassandraKeyValueStore;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.TimelineDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TimelineElementEntityId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty( name = TimelineDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA )
public class CassandraTimelineEntityDao
        extends AbstractCassandraKeyValueStore<TimelineElementEntityId, TimelineElementEntity>
        implements TimelineEntityDao {

    private final CassandraOperations cassandraTemplate;

    public CassandraTimelineEntityDao(CassandraOperations cassandraTemplate) {
        super( cassandraTemplate, TimelineElementEntity.class);
        this.cassandraTemplate = cassandraTemplate;
    }

    @Override
    public Set<TimelineElementEntity> findByIun(String iun ) {
        return new HashSet<>( cassandraTemplate.select( queryByIun(iun), TimelineElementEntity.class ) );
    }

    @Override
    public void deleteByIun(String iun) {
        cassandraTemplate.delete( queryByIun(iun), TimelineElementEntity.class );
    }

    private Query queryByIun(String iun) {
        return Query.query(Criteria.where("iun").is(iun));
    }
}
