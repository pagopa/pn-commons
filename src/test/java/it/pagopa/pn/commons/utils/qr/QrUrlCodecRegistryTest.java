package it.pagopa.pn.commons.utils.qr;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QrUrlCodecRegistryTest {
    private QrUrlCodecRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new QrUrlCodecRegistry();
    }

    @Test
    void testRegisterAndGetDefaultCodec() {
        QrUrlCodec codec = mock(QrUrlCodec.class);
        when(codec.getVersion()).thenReturn(new Version(1, 0, 0));

        registry.register(codec);

        QrUrlCodec defaultCodec = registry.getDefaultCodec();
        assertNotNull(defaultCodec);
        assertEquals(codec, defaultCodec);
    }

    @Test
    void testRegisterNullCodecThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> registry.register(null));
    }

    @Test
    void testRegisterCodecWithNullVersionThrowsException() {
        QrUrlCodec codec = mock(QrUrlCodec.class);
        when(codec.getVersion()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> registry.register(codec));
    }

    @Test
    void testRegisterDuplicateVersionThrowsException() {
        QrUrlCodec codec1 = mock(QrUrlCodec.class);
        QrUrlCodec codec2 = mock(QrUrlCodec.class);
        Version version = new Version(1, 0, 0);

        when(codec1.getVersion()).thenReturn(version);
        when(codec2.getVersion()).thenReturn(version);

        registry.register(codec1);
        assertThrows(IllegalArgumentException.class, () -> registry.register(codec2));
    }
    @Test
    void register_shouldUpdateDefaultVersionIfNewer() {
        QrUrlCodecRegistry registry = new QrUrlCodecRegistry();

        QrUrlCodec codec1 = new QrUrlCodecV1(mock(ParameterConsumer.class), mock(ObjectMapper.class));
        QrUrlCodec codec2 = new QrUrlCodecV2();

        registry.register(codec1);
        // defaultVersion dovrebbe essere 1.0.0

        registry.register(codec2);
        // defaultVersion dovrebbe essere aggiornata a 2.0.0

        // Reflection per accedere a defaultVersion (se non c'è un getter)
        Version defaultVersion = null;
        try {
            var field = QrUrlCodecRegistry.class.getDeclaredField("defaultVersion");
            field.setAccessible(true);
            defaultVersion = (Version) field.get(registry);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }

        assertEquals(new Version(2, 0, 0), defaultVersion, "defaultVersion should be updated to the latest registered version");
    }

    public static class QrUrlCodecV2 implements QrUrlCodec{

        @Override
        public String encode(String qrToken, UrlData data) {
            return "";
        }

        public String decode(String encoded) {
            // Simula una decodifica base64 per test
            return new String(java.util.Base64.getDecoder().decode(encoded));
        }
        public Version getVersion() {
            return new Version(2, 0, 0);
        }

        @Override
        public boolean canHandle(String url) {
            return false;
        }
    }
}
