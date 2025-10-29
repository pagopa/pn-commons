package it.pagopa.pn.commons.utils.qr;

import it.pagopa.pn.commons.utils.qr.models.Version;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Getter
@Slf4j
public class QrUrlCodecRegistry {
    private final Map<Version, QrUrlCodec> codecs = new TreeMap<>();
    private Version defaultVersion;

    public void register(QrUrlCodec codec) {
        if (codec == null || codec.getVersion() == null) {
            throw new IllegalArgumentException("Codec or version cannot be null.");
        }
        if(codecs.containsKey(codec.getVersion())) {
            throw new IllegalArgumentException("Codec for version " + codec.getVersion() + " is already registered.");
        }
        codecs.put(codec.getVersion(), codec);

        // Logica per determinare la versione di default (quella più recente)
        if (defaultVersion == null || codec.getVersion().compareTo(defaultVersion) > 0) {
            defaultVersion = codec.getVersion();
        }
    }

    /**
     * Ritorna la versione di default del codec (la più recente registrata).
     *
     * @return la versione di default
     **/
    public QrUrlCodec getDefaultCodec() {
        return codecs.get(defaultVersion);
    }

    /**
     * Decodifica l'URL utilizzando tutti i codec censiti nel registro partendo dalla versione più recente.
     *
     * @param url l'URL da decodificare
     * @return la stringa decodificata
     * @throws IllegalArgumentException se nessun codec può gestire l'URL fornito
     **/
    public String decodeWithAppropriateCodec(String url) {
        return codecs.values().stream()
                .sorted((a, b) -> b.getVersion().compareTo(a.getVersion()))
                .map(codec -> {
                    try {
                        String qr = codec.decode(url);
                        log.debug("URL {} decoded successfully with codec version {}", url, codec.getVersion());
                        return qr;
                    } catch (IllegalArgumentException e) {
                        log.debug("Codec for version {} cannot decode the URL {}: {}", codec.getVersion(), url, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No codec version can handle given URL: " + url));
    }
}
