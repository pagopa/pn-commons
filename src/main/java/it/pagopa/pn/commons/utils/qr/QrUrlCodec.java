package it.pagopa.pn.commons.utils.qr;

import it.pagopa.pn.commons.utils.qr.models.UrlData;
import it.pagopa.pn.commons.utils.qr.models.Version;

import java.util.regex.Pattern;

public interface QrUrlCodec {
        String VERSION_MARKER = "_VERSION_";
        Pattern VERSION_PATTERN = Pattern.compile(VERSION_MARKER + "(\\d+\\.\\d+\\.\\d+)");
        String PARAMETER_NAME = "AARQrUrlConfigs";
        /**
         * Costruisce una URL contenente il QR code
         */
        String encode(String qrToken, UrlData data);

        /**
         * Decodifica una URL per estrapolare il QR code
         */
        String decode(String url) throws IllegalArgumentException;

        /**
         * Ritorna la versione supportata dal codec
         */
        Version getVersion();

        /**
         * Verifica se il codec può gestire l'URL passato come parametro
         *
         * @param url URL da verificare
         * @return true se il codec può gestire l'URL, false altrimenti
         */
        boolean canHandle(String url);
}
