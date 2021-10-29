package it.pagopa.pn.commons_delivery.middleware;


import it.pagopa.pn.api.dto.notification.directaccesstoken.DirectAccessToken;
import it.pagopa.pn.commons.abstractions.IdConflictException;

import java.util.Optional;

public interface DirectAccessTokenDao {
    static final String IMPLEMENTATION_TYPE_PROPERTY_NAME = "pn.middleware.impl.direct-access-token-dao";

    void addDirectAccessToken(DirectAccessToken directAccessToken) throws IdConflictException;

    Optional<DirectAccessToken> getDirectAccessToken(String token);
}
