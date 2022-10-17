package it.pagopa.pn.commons.utils.ssl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.apache.http.ssl.SSLContextBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Component
public class SSLContextFactory {

    private static final char[] KEYSTORE_PASSWORD = "password".toCharArray();

    public SSLContext buildSSLHttpClient(String clientCertificatePem, String clientKeyPem, List<String> trustedServerCertificates) {

        SSLContext sslContext;
        try {
            KeyStore keyStore = createKeyStore(clientCertificatePem, clientKeyPem);
            KeyStore trustStore = createTrustStore(trustedServerCertificates);

            sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(trustStore, new SystemTrustStrategy())
                    .loadKeyMaterial(keyStore, KEYSTORE_PASSWORD)
                    .build();

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException
                | UnrecoverableKeyException | KeyManagementException e)
        {
            throw new PnInternalException(e.getMessage(), e);
        }
        return sslContext;
    }

    @NotNull
    private KeyStore createTrustStore(List<String> trustedServerCertificates) throws KeyStoreException,
            CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore trustStore = newKeyStore();
        for( String trustServer : trustedServerCertificates) {
            trustStore.setCertificateEntry( "server_ca", buildX509CertificateFromPemString(trustServer) );
        }
        return trustStore;
    }

    @NotNull
    private KeyStore createKeyStore(String clientCertificatePem, String clientKeyPem) throws KeyStoreException,
            CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = newKeyStore();
        X509Certificate clientCert = buildX509CertificateFromPemString(clientCertificatePem);
        keyStore.setCertificateEntry("client_cert", clientCert);
        keyStore.setKeyEntry("client_key", buildKeyFromPemString(clientKeyPem),
                KEYSTORE_PASSWORD,
                new Certificate[]{clientCert});
        return keyStore;
    }

    private KeyStore newKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, KEYSTORE_PASSWORD);
        OutputStream outputStream = OutputStream.nullOutputStream();
        ks.store(outputStream, KEYSTORE_PASSWORD);
        return ks;
    }

    private X509Certificate buildX509CertificateFromPemString(String stringCert) throws CertificateException {
        InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(stringCert));
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return (X509Certificate) cf.generateCertificate(is);
    }

    private PrivateKey buildKeyFromPemString(String clientKeyPem) {
        InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(clientKeyPem));
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(is.readAllBytes());
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new PnInternalException("Error building private key from client key pem", e);
        }
    }
}
