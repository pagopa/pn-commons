package it.pagopa.pn.commons_delivery.middleware.directaccesstokendao;

import it.pagopa.pn.api.dto.notification.directaccesstoken.DirectAccessToken;
import it.pagopa.pn.commons_delivery.model.notification.cassandra.TokenEntity;
import org.springframework.stereotype.Component;

@Component
public class DtoToEntityDirectAccessTokenMapper {

    public TokenEntity dto2Entity(DirectAccessToken directAccessToken) {
        return TokenEntity.builder()
                .token( directAccessToken.getToken() )
                .iun( directAccessToken.getIun() )
                .taxId( directAccessToken.getTaxId() )
                .build();
    }
}
