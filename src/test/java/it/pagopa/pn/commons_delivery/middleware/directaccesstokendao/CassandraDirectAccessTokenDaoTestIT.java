package it.pagopa.pn.commons_delivery.middleware.directaccesstokendao;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.api.dto.notification.directaccesstoken.DirectAccessToken;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.DirectAccessTokenDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        DirectAccessTokenDao.IMPLEMENTATION_TYPE_PROPERTY_NAME + "=" + MiddlewareTypes.CASSANDRA,
        "spring.data.cassandra.keyspace-name=pn_delivery_test",
        "spring.data.cassandra.cluster-name=cassandra",
        "spring.data.cassandra.local-datacenter=datacenter1",
        "spring.data.cassandra.contact-points=localhost",
        "spring.data.cassandra.username=cassandra",
        "spring.data.cassandra.password=cassandra",
        "spring.data.cassandra.schema-action=CREATE_IF_NOT_EXISTS"
})
@ContextConfiguration(classes = {
        CassandraDirectAccessTokenDao.class,
        CassandraDirectAccessTokenEntityDao.class,
        CassandraDirectAccessTokenDaoTestIT.TestContext.class,
        DtoToEntityDirectAccessTokenMapper.class,
        EntityToDtoDirectAccessTokenMapper.class,
        CassandraAutoConfiguration.class,
        CassandraDataAutoConfiguration.class
})
@EntityScan(basePackages = {"it.pagopa.pn"})
class CassandraDirectAccessTokenDaoTestIT {

    @Autowired
    private CassandraDirectAccessTokenDao dao;

    @Test
    void testSimple() throws IdConflictException {
        //Given
        String token = UUID.randomUUID().toString();
        String iun = UUID.randomUUID().toString();
        String taxId = "CGNNMO80A03H501U";

        DirectAccessToken dat = DirectAccessToken.builder()
                .token( token )
                .iun( iun )
                .taxId( taxId )
                .build();

        dao.addDirectAccessToken(dat);

        Optional<DirectAccessToken> result = dao.getDirectAccessToken( token );

        final DirectAccessToken directAccessToken = result.get();
        Assertions.assertNotNull( result );
        Assertions.assertEquals( token , directAccessToken.getToken() );
        Assertions.assertEquals( iun, directAccessToken.getIun() );
        Assertions.assertEquals( taxId, directAccessToken.getTaxId() );
    }

    @Configuration
    @EntityScan(basePackages = {"it.pagopa.pn"})
    public static class TestContext {

        @Bean
        public ObjectMapper objMapper() {
            return new ObjectMapper();
        }
    }
}
