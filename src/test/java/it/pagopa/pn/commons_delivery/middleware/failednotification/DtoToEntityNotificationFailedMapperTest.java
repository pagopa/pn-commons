package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.api.dto.notification.failednotification.PaperNotificationFailed;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoToEntityNotificationFailedMapperTest {
    private DtoToEntityNotificationFailedMapper dtoToEntityNotificationFailedMapper;

    @BeforeEach
    void instantiateDao() {
        dtoToEntityNotificationFailedMapper = new DtoToEntityNotificationFailedMapper();
    }

    @Test
    void dto2Entity() {
        String iun = "202109-eb10750e-e876-4a5a-8762-c4348d679d35";
        String idRecipient = "paMi2";

        PaperNotificationFailed failedNot = PaperNotificationFailed.builder()
                .iun(iun)
                .recipientId(idRecipient).build();

        PaperNotificationFailedEntity entity = dtoToEntityNotificationFailedMapper.dto2Entity(failedNot);

        assertEquals(failedNot.getIun(), entity.getId().getIun());
        assertEquals(failedNot.getRecipientId(), entity.getId().getRecipientId());
    }
}