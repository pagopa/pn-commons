package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.status.RequestUpdateStatusDto;
import it.pagopa.pn.api.dto.status.ResponseUpdateStatusDto;
import org.springframework.http.ResponseEntity;

//TODO Da portare in Pn-delivery-push
public interface PnDeliveryClient {
    ResponseEntity<ResponseUpdateStatusDto> updateState(RequestUpdateStatusDto dto);
}
