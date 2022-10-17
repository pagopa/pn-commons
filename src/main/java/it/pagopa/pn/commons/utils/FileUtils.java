package it.pagopa.pn.commons.utils;

import org.apache.commons.codec.binary.Hex;
import org.springframework.util.Base64Utils;

public abstract class FileUtils {
    private FileUtils() {}

    public static String convertBase64toHex(String base64String) {
        byte[] decoded = Base64Utils.decodeFromString(base64String);
        return Hex.encodeHexString(decoded);
    }

    public static String convertBase64toHexUppercase(String base64String) {
        return convertBase64toHex(base64String).toUpperCase();
    }
}
