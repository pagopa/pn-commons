package it.pagopa.pn.commons_delivery.utils;

import it.pagopa.pn.api.dto.notification.status.NotificationStatus;
import it.pagopa.pn.api.dto.status.RequestUpdateStatusDto;
import it.pagopa.pn.api.dto.status.ResponseUpdateStatusDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class PnDeliveryClientImplTest {
    @Mock
    private RestTemplate restTemplate;
    
    private PnDeliveryClientImpl pnDeliveryClient;

    @BeforeEach
    public void setup() {
        pnDeliveryClient = new PnDeliveryClientImpl(restTemplate);
    }
    
    @ExtendWith(MockitoExtension.class)
    @Test
    void updateState() {
        RequestUpdateStatusDto dto = RequestUpdateStatusDto.builder()
                .iun("iun")
                .build();
        ResponseUpdateStatusDto res = ResponseUpdateStatusDto.builder()
                .currentStatus(NotificationStatus.DELIVERING)
                .nextStatus(NotificationStatus.DELIVERED)
                .build();
        ResponseEntity<ResponseUpdateStatusDto> responseMock = ResponseEntity.ok(res);
        
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), Mockito.<Class<ResponseUpdateStatusDto>>any())).thenReturn(responseMock);
                
        ResponseEntity<ResponseUpdateStatusDto> response = pnDeliveryClient.updateState(dto);

        Assertions.assertEquals(response.getBody(),res);
    }
}
