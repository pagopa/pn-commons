package it.pagopa.pn.commons.utils.qr;

import it.pagopa.pn.commons.utils.qr.models.Version;
import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
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
        // Attualmente ritorna il codec per la versione 1.0.0, ma può essere modificato per supportare versioni future.
        // return codecs.get(defaultVersion);
        return codecs.get(new Version(1, 0, 0));
    }

//    Metodo da utilizzare in caso si decida di versionare il sistema di codifica.
//    public QrUrlCodec findCodecForUrl(String url) {
//        // Cerca il codec più adatto per l'URL, partendo dalla versione più recente
//        return codecs.values().stream()
//                .sorted((a, b) -> b.getVersion().compareTo(a.getVersion()))
//                .filter(codec -> codec.canHandle(url))
//                .findFirst()
//                .orElse(null);
//    }
}
