package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.status.RequestUpdateStatusDto;
import it.pagopa.pn.api.dto.status.ResponseUpdateStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//TODO Da portare in Pn-delivery-push
@Slf4j
@Component
public class PnDeliveryClientImpl implements PnDeliveryClient {
    private final RestTemplate restTemplate;
    private final String PN_DELIVERY_BASE_URL ="http://localhost:8080/delivery-private"; //TODO Portare nel file di property di pn-delivery-push
    private final String UPDATE_STATUS_URL ="/notifications/update-status";

    public PnDeliveryClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<ResponseUpdateStatusDto> updateState(RequestUpdateStatusDto dto) {
        log.debug("Start update status call for iun {}", dto.getIun());

        final String baseUrl = PN_DELIVERY_BASE_URL + UPDATE_STATUS_URL;
        HttpEntity<RequestUpdateStatusDto> entity = new HttpEntity<>(dto, null);

        ResponseEntity<ResponseUpdateStatusDto> resp = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, ResponseUpdateStatusDto.class);

        log.debug("Response update state for iun {} is {}", dto.getIun(), resp);

        return resp;
    }
}
