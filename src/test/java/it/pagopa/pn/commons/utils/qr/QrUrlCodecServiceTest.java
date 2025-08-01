package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import it.pagopa.pn.commons.utils.qr.models.UrlData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QrUrlCodecServiceTest {

    private ParameterConsumer parameterConsumer;
    private QrUrlCodec codec;
    private UrlData urlData;
    private QrUrlCodecService qrUrlCodecService;

    @BeforeEach
    void setUp() {
        parameterConsumer = mock(ParameterConsumer.class);
        codec = mock(QrUrlCodec.class);
        urlData = mock(UrlData.class);
        ObjectMapper mapper = new ObjectMapper();
        qrUrlCodecService = new QrUrlCodecService(parameterConsumer, mapper);
    }

    @Test
    void testEncode_DelegatesToDefaultCodec(){
        String qrToken = "token";
        String expected = "http://cittadini.dev"+"/appio/notifica?aarQr="+qrToken;

        String parameters = "{\"1.0.0\":{\"directAccessUrlTemplatePhysical\":\"http://cittadini.dev\"" +
                ", \"directAccessUrlTemplateLegal\":\"http://imprese.dev\"" +
                ", \"quickAccessUrlAarDetailSuffix\":\"/appio/notifica?aarQr\"}}";

        when(codec.encode(qrToken, urlData)).thenReturn(expected);
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(parameters));
        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PF);
        // encode deve restituire il valore atteso
        String result = qrUrlCodecService.encode(qrToken, urlData);
        assertEquals(expected, result);
    }

    @Test
    void testDecode_DelegatesToDefaultCodec(){
        String qrUrl = "http://cittadini.dev/appio/notifica?aarQr=token";
        String expectedToken = "token";
        String parameters = "{\"1.0.0\":{\"directAccessUrlTemplatePhysical\":\"http://cittadini.dev\"" +
                ", \"directAccessUrlTemplateLegal\":\"http://imprese.dev\"" +
                ", \"quickAccessUrlAarDetailSuffix\":\"/appio/notifica?aarQr\"}}";

        when(codec.decode(qrUrl)).thenReturn(expectedToken);
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(parameters));
        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PF);

        // decode deve restituire il valore atteso
        String result = qrUrlCodecService.decode(qrUrl);

        assertEquals(expectedToken, result);
    }
}
