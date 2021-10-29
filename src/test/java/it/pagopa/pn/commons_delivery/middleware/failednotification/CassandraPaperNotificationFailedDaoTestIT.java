package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.api.dto.notification.failednotification.PaperNotificationFailed;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        PaperNotificationFailedDao.IMPLEMENTATION_TYPE_PROPERTY_NAME + "=" + MiddlewareTypes.CASSANDRA,
        "spring.data.cassandra.keyspace-name=pn_delivery_test",
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
})
@EntityScan(basePackages = {"it.pagopa.pn"})
class CassandraPaperNotificationFailedDaoTestIT {
    @Autowired
    private PaperNotificationFailedDao specificDao;
    @Autowired
    private CassandraPaperNotificationFailedEntityDao daoEntity;

    @Test
    void addPaperNotificationOk() throws IdConflictException {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d38";
        String idRecipient = "paMi3";

        deletePaperNotificationFailed(iun, idRecipient);

        PaperNotificationFailed failedNot = PaperNotificationFailed.builder()
                .iun(iun)
                .recipientId(idRecipient).build();
        specificDao.addPaperNotificationFailed(failedNot);

        Set<PaperNotificationFailed> res = specificDao.getNotificationByRecipientId(idRecipient)
                .stream()
                .filter(pnf -> iun.equals(pnf.getIun()))
                .filter(pnf -> idRecipient.equals(pnf.getRecipientId()))
                .collect(Collectors.toSet());

        assertEquals(1, res.size());
    }

    @Test
    void deleteNotificationFailedOk() throws IdConflictException {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d41";
        String idRecipient = "paMi4";

        PaperNotificationFailed failedNot = PaperNotificationFailed.builder()
                .iun(iun)
                .recipientId(idRecipient).build();

        specificDao.addPaperNotificationFailed(failedNot);

        deletePaperNotificationFailed(iun, idRecipient);

        Set<PaperNotificationFailed> res = specificDao.getNotificationByRecipientId(idRecipient)
                .stream()
                .filter(pnf -> iun.equals(pnf.getIun()))
                .filter(pnf -> idRecipient.equals(pnf.getRecipientId()))
                .collect(Collectors.toSet());

        assertEquals(0, res.size());
    }

    private void deletePaperNotificationFailed(String iun, String idRecipient) {
        specificDao.deleteNotificationFailed(idRecipient, iun);
    }

}