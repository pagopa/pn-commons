package it.pagopa.pn.commons_delivery.middleware.pagination;


import it.pagopa.pn.api.dto.notification.failednotification.PaperNotificationFailed;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.failednotification.*;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        PaperNotificationFailedDao.IMPLEMENTATION_TYPE_PROPERTY_NAME + "=" + MiddlewareTypes.CASSANDRA,
        "spring.data.cassandra.keyspace-name=pn_delivery_local",
        "spring.data.cassandra.cluster-name=cassandra",
        "spring.data.cassandra.local-datacenter=datacenter1",
        "spring.data.cassandra.contact-points=localhost",
        "spring.data.cassandra.username=cassandra",
        "spring.data.cassandra.password=cassandra",
        "spring.data.cassandra.schema-action=CREATE_IF_NOT_EXISTS"
})
@ContextConfiguration(classes = {
        CassandraPaperNotificationFailedDao.class,
        CassandraPaperNotificationFailedEntityDao.class,
        DtoToEntityNotificationFailedMapper.class,
        EntityToDtoNotificationFailedMapper.class,
        CassandraAutoConfiguration.class,
        CassandraDataAutoConfiguration.class,
        CassandraPagination.class
})
@EntityScan(basePackages = {"it.pagopa.pn"})
class CassandraPaginationTest {
    @Autowired
    private CassandraPagination cassandraPagination;
    @Autowired
    private PaperNotificationFailedDao specificDao;

    @Test
    void paginationExample() {
        String idRecipient = "paMi4";

        for (int iun = 0; iun < 50; iun++) {
            setupEnv(Integer.toString(iun), idRecipient);
        }

        List<PaperNotificationFailedEntity> sliceList = cassandraPagination.paginationCassandra(idRecipient, 10, 3);

    }

    private void setupEnv(String iun, String idRecipient) {
        deletePaperNotificationFailed(iun, idRecipient);

        PaperNotificationFailed failedNot = PaperNotificationFailed.builder()
                .iun(iun)
                .recipientId(idRecipient).build();
        specificDao.addPaperNotificationFailed(failedNot);
    }

    private void deletePaperNotificationFailed(String iun, String idRecipient) {
        specificDao.deleteNotificationFailed(idRecipient, iun);
    }

}