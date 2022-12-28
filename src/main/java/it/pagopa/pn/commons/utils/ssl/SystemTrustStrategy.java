package it.pagopa.pn.commons.utils.ssl;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.stereotype.Component;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
@Component
public class SystemTrustStrategy implements TrustStrategy {

    private final X509TrustManager systemX509TrustManager;

    public SystemTrustStrategy() throws NoSuchAlgorithmException, KeyStoreException {
        this.systemX509TrustManager = retrieveSystemTrustManager();
    }

    public X509Certificate[] getAcceptedIssuers(){
        return this.systemX509TrustManager.getAcceptedIssuers();
    }

    private static X509TrustManager retrieveSystemTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Using null here initialises the TMF with the default trust store.
        tmf.init((KeyStore) null);

        // Get hold of the default trust manager
        X509TrustManager defaultTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager x509TrustManager) {
                defaultTm = x509TrustManager;
                break;
            }
        }
        return defaultTm;
    }

    @Override
    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        log.debug( "Called with authType={}", authType );
        for ( X509Certificate c : chain ) {
            log.debug( "Certificate name={}", c.getSubjectX500Principal().getName() );
        }

        boolean trusted;
        try {
             this.systemX509TrustManager.checkServerTrusted( chain, authType );
             trusted = true;
        } catch (CertificateException e) {
            log.warn( "Certificate not trusted by default TrustManager", e );
            trusted = false;
        }

        return trusted;
    }
}
