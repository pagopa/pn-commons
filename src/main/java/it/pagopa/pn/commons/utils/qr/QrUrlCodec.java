package it.pagopa.pn.commons.utils.qr;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.commons.utils.qr.models.UrlData;
import it.pagopa.pn.commons.utils.qr.models.Version;

public interface QrUrlCodec {
        String PARAMETER_NAME = "AARQrUrlConfigs";
        /**
         * Costruisce una URL contenente il QR code
         */
        String encode(String qrToken, UrlData data) throws IllegalArgumentException, PnInternalException;

        /**
         * Decodifica una URL per estrapolare il QR code
         */
        String decode(String url) throws IllegalArgumentException, PnInternalException;

        /**
         * Ritorna la versione supportata dal codec
         */
        Version getVersion();
}
