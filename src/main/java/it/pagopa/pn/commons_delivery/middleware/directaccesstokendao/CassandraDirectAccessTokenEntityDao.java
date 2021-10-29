package it.pagopa.pn.commons_delivery.middleware.directaccesstokendao;


import it.pagopa.pn.commons.abstractions.impl.AbstractCassandraKeyValueStore;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.DirectAccessTokenDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TokenEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty( name = DirectAccessTokenDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA )
public class CassandraDirectAccessTokenEntityDao extends AbstractCassandraKeyValueStore<String, TokenEntity> {

    public CassandraDirectAccessTokenEntityDao(CassandraOperations cassandraTemplate) {
        super(cassandraTemplate, TokenEntity.class);
    }
}
