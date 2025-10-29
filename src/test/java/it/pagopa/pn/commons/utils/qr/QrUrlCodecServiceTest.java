package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import it.pagopa.pn.commons.utils.qr.models.UrlData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QrUrlCodecServiceTest {

    private ParameterConsumer parameterConsumer;
    private QrUrlCodecService qrUrlCodecService;

    @BeforeEach
    void setUp() {
        parameterConsumer = mock(ParameterConsumer.class);
        qrUrlCodecService = new QrUrlCodecService(parameterConsumer, new ObjectMapper());
    }

    @Test
    void testEncode_DelegatesToDefaultCodec(){
        // Testiamo l'encoding che sfrutterà il default codec (quello con la versione maggiore dichiarata). In questo caso 1.0.0.
        String qrToken = "token";
        String expected = "http://cittadini.dev"+"/appio/notifica?aarQr="+qrToken;

        String parameters = """
        {
          "0.9.0": {
            "directAccessUrlTemplatePhysical": "http://cittadini.dev",
            "directAccessUrlTemplateLegal": "http://imprese.dev",
            "quickAccessUrlAarDetailSuffix": "/appio/notifica-0.0.9?aarQr"
          },
          "1.0.0": {
            "directAccessUrlTemplatePhysical": "http://cittadini.dev",
            "directAccessUrlTemplateLegal": "http://imprese.dev",
            "quickAccessUrlAarDetailSuffix": "/appio/notifica?aarQr"
          }
        }
        """;

        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(parameters));

        UrlData urlData = new UrlData();
        urlData.setRecipientType(RecipientTypeInt.PF);
        String result = qrUrlCodecService.encode(qrToken, urlData);
        assertEquals(expected, result);
    }

    @ParameterizedTest()
    @CsvSource( value= {
            "http://cittadini.dev/appio/notifica-0.0.9?aarQr=token,token",
            "http://cittadini.dev/appio/notifica-1.0.0?aarQr=token,token"
    })
    void testDecode_FindsDecoder(String qrUrl, String expectedToken){
        String parameters = """
        {
          "0.9.0": {
            "directAccessUrlTemplatePhysical": "http://cittadini.dev",
            "directAccessUrlTemplateLegal": "http://imprese.dev",
            "quickAccessUrlAarDetailSuffix": "/appio/notifica-0.0.9?aarQr"
          },
          "1.0.0": {
            "directAccessUrlTemplatePhysical": "http://cittadini.dev",
            "directAccessUrlTemplateLegal": "http://imprese.dev",
            "quickAccessUrlAarDetailSuffix": "/appio/notifica-1.0.0?aarQr"
          }
        }
        """;

        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(parameters));

        // decode deve restituire il valore atteso
        String result = qrUrlCodecService.decode(qrUrl);

        assertEquals(expectedToken, result);
    }

    @Test
    void testDecode_DoesNotFindDecoder(){
        String qrUrl = "http://cittadini.dev/appio/notifica-2.0.0?aarQr=token";
        String parameters = """
        {
          "0.9.0": {
            "directAccessUrlTemplatePhysical": "http://cittadini.dev",
            "directAccessUrlTemplateLegal": "http://imprese.dev",
            "quickAccessUrlAarDetailSuffix": "/appio/notifica-0.0.9?aarQr"
          },
          "1.0.0": {
            "directAccessUrlTemplatePhysical": "http://cittadini.dev",
            "directAccessUrlTemplateLegal": "http://imprese.dev",
            "quickAccessUrlAarDetailSuffix": "/appio/notifica-1.0.0?aarQr"
          }
        }
        """;

        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(parameters));

        // decode deve restituire il valore atteso
        Assertions.assertThrows(IllegalArgumentException.class, () -> qrUrlCodecService.decode(qrUrl));
    }
}
