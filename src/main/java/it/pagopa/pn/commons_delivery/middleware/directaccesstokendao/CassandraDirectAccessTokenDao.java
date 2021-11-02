package it.pagopa.pn.commons_delivery.middleware.directaccesstokendao;

import it.pagopa.pn.api.dto.notification.directaccesstoken.DirectAccessToken;
import it.pagopa.pn.commons.abstractions.IdConflictException;
import it.pagopa.pn.commons.abstractions.KeyValueStore;
import it.pagopa.pn.commons.abstractions.impl.MiddlewareTypes;
import it.pagopa.pn.commons_delivery.middleware.DirectAccessTokenDao;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TokenEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnProperty(name = DirectAccessTokenDao.IMPLEMENTATION_TYPE_PROPERTY_NAME, havingValue = MiddlewareTypes.CASSANDRA)
@Slf4j
public class CassandraDirectAccessTokenDao implements DirectAccessTokenDao {

    private final KeyValueStore<String, TokenEntity> tokenEntityDao;
    private final DtoToEntityDirectAccessTokenMapper dto2entityMapper;
    private final EntityToDtoDirectAccessTokenMapper entity2dtoMapper;

    public CassandraDirectAccessTokenDao(
            KeyValueStore<String, TokenEntity> tokenEntityDao,
            DtoToEntityDirectAccessTokenMapper dto2entityMapper,
            EntityToDtoDirectAccessTokenMapper entity2dtoMapper) {
        this.tokenEntityDao = tokenEntityDao;
        this.dto2entityMapper = dto2entityMapper;
        this.entity2dtoMapper = entity2dtoMapper;
    }

    @Override
    public void addDirectAccessToken(DirectAccessToken directAccessToken) throws IdConflictException {
        TokenEntity entity = dto2entityMapper.dto2Entity(directAccessToken);
        tokenEntityDao.putIfAbsent(entity);
    }

    @Override
    public Optional<DirectAccessToken> getDirectAccessToken(String token) {
        return tokenEntityDao.get(token)
                .map(entity2dtoMapper::entity2Dto);
    }





}
