package it.pagopa.pn.commons.utils.ssl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SSLContextFactoryTest {

    SSLContextFactory sslContextFactory;

    @BeforeEach
    void setup() {
        this.sslContextFactory = new SSLContextFactory();
    }

    @Test
    void buildContextFactorySuccess() {
        //Given
        String clientCertificatePem = loadMockClientCert();
        String clientKeyPem = loadMockClientKey();
        List<String> trustedServerCertificates = Collections.singletonList( loadMockServerCert() );

        //When
        SSLContext result = sslContextFactory.buildSSLHttpClient( clientCertificatePem, clientKeyPem, trustedServerCertificates );

        //Then
        assertNotNull(result);
    }

    static String loadMockClientCert() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("/Users/alessandromasci/Desktop/mockserver/mockserver.client.pem"));
            return String.join("", lines.subList( 1, lines.size() - 1 ));
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    static String loadMockClientKey() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("/Users/alessandromasci/Desktop/mockserver/mockserver.client.key8.pem"));
            return String.join("", lines.subList( 1, lines.size() - 1 ));
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    static String loadMockServerCert() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("/Users/alessandromasci/Desktop/mockserver/CertificateAuthorityCertificate.pem"));
            return String.join("", lines.subList( 1, lines.size() - 1 ));
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }
}
