package it.pagopa.pn.commons.utils;

import org.apache.commons.codec.binary.Hex;

import java.util.Base64;

// siamo nel caso in cui è una libreria, la classe viene estesa da chi la utilizza
@java.lang.SuppressWarnings("java:S1610")
public abstract class FileUtils {
    private FileUtils() {}

    public static String convertBase64toHex(String base64String) {
        byte[] decoded = Base64.getDecoder().decode(base64String);
        return Hex.encodeHexString(decoded);
    }

    public static String convertBase64toHexUppercase(String base64String) {
        return convertBase64toHex(base64String).toUpperCase();
    }
}
