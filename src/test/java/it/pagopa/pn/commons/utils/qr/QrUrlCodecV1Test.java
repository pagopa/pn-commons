package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QrUrlCodecV1Test {
    private ParameterConsumer parameterConsumer;
    private QrUrlCodecV1 codec;
    private UrlData urlData;

    private final String configJson = """
        {
          "1.0.0": {
            "directAccessUrlTemplatePhysical": "http://physical",
            "directAccessUrlTemplateLegal": "http://legal",
            "quickAccessUrlAarDetailSuffix": "/detail?token"
          }
        }
        """;

    @BeforeEach
    void setUp() {
        parameterConsumer = mock(ParameterConsumer.class);
        ObjectMapper objectMapper = new ObjectMapper();
        codec = new QrUrlCodecV1(parameterConsumer, objectMapper);
        urlData = mock(UrlData.class);
        QrUrlConfigs urlConfigs = mock(QrUrlConfigs.class);
    }

    @Test
    void encode_shouldReturnCorrectUrlForPhysical() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(configJson));

        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PF);

        String result = codec.encode("abc123", urlData);

        assertEquals("http://physical/detail?token=abc123", result);
    }

    @Test
    void encode_shouldReturnCorrectUrlForLegal() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(configJson));

        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PG);

        String result = codec.encode("xyz789", urlData);

        assertEquals("http://legal/detail?token=xyz789", result);
    }

    @Test
    void encode_shouldThrowIfParameterMissing() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.empty());

        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PF);

        assertThrows(IllegalArgumentException.class, () -> codec.encode("abc", urlData));
    }

    @Test
    void decode_shouldReturnTokenForPhysical() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(configJson));

        String url = "http://physical/detail?token=abc123";
        String token = codec.decode(url);

        assertEquals("abc123", token);
    }

    @Test
    void decode_shouldReturnTokenForLegal() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(configJson));

        String url = "http://legal/detail?token=xyz789";
        String token = codec.decode(url);

        assertEquals("xyz789", token);
    }

    @Test
    void decode_shouldThrowIfUrlInvalid() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(configJson));

        assertThrows(IllegalArgumentException.class, () -> codec.decode("http://unknown/detail?token=abc"));
    }

    @Test
    void canHandle_shouldReturnTrueForValidUrl() {
        String url = "http://physical/detail?token=abc123_VERSION_1.0.0";
        assertTrue(codec.canHandle(url));
    }

    @Test
    void canHandle_shouldReturnFalseForNull() {
        String url="";
        assertFalse(codec.canHandle(url));
    }

    @Test
    void encode_shouldThrowIfParamValueIsEmpty() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.empty());

        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PG);

        assertThrows(IllegalArgumentException.class, () -> codec.encode("token", urlData));
    }

    @Test
    void encode_shouldThrowIfUrlConfigIsNull() {
        // Simula un JSON che non contiene la versione richiesta
        String invalidConfigJson = """
        {
          "2.0.0": {
            "directAccessUrlTemplatePhysical": "http://other",
            "directAccessUrlTemplateLegal": "http://other",
            "quickAccessUrlAarDetailSuffix": "/other"
          }
        }
        """;
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(invalidConfigJson));

        when(urlData.getRecipientType()).thenReturn(RecipientTypeInt.PF);

        assertThrows(NullPointerException.class, () -> codec.encode("token", urlData));
    }

    @Test
    void decode_shouldThrowIfNeitherPGorPF() {
        when(parameterConsumer.getParameterValue(anyString(), eq(String.class)))
                .thenReturn(Optional.of(configJson));
        String url = "http://XXX/detail?token=abc123";
        assertThrows(IllegalArgumentException.class, () -> codec.decode(url));
    }
}