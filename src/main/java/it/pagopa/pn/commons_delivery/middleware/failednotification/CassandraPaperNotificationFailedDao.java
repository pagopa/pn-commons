package it.pagopa.pn.commons_delivery.middleware.failednotification;

import it.pagopa.pn.api.dto.notification.failednotification.PaperNotificationFailed;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntity;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.PaperNotificationFailedEntityId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = PaperNotificationFailedDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
public class CassandraPaperNotificationFailedDao implements PaperNotificationFailedDao {

    private PaperNotificationFailedEntityDao dao;
    private DtoToEntityNotificationFailedMapper dtoToEntity;
    private EntityToDtoNotificationFailedMapper entityToDto;

    public CassandraPaperNotificationFailedDao(PaperNotificationFailedEntityDao dao,
                                               DtoToEntityNotificationFailedMapper dtoToEntity,
                                               EntityToDtoNotificationFailedMapper entityToDto) {
        this.dao = dao;
        this.dtoToEntity = dtoToEntity;
        this.entityToDto = entityToDto;
    }

    @Override
    public void addPaperNotificationFailed(PaperNotificationFailed paperNotificationFailed) throws IdConflictException {
        PaperNotificationFailedEntity entity = dtoToEntity.dto2Entity(paperNotificationFailed);
        dao.put(entity);
    }

    @Override
    public Set<PaperNotificationFailed> getNotificationByRecipientId(String recipientId) {
        return dao.findByRecipientId(recipientId)
                .stream().map(entityToDto::entityToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteNotificationFailed(String recipientId, String iun) {
        dao.delete(PaperNotificationFailedEntityId.builder()
                .iun(iun)
                .recipientId(recipientId)
                .build());
    }
}
