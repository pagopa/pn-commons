package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUtilsTest {

    @Test
    void convertBase64toHex() {
        //Given
        String base64String = "c3RyaW5nYSBkaSB0ZXN0IGRhIGNvbnZlcnRpcmUgaW4gYmFzZTY0";

        //When
        String result = FileUtils.convertBase64toHex(base64String);

        //Then
        assertEquals("737472696e6761206469207465737420646120636f6e7665727469726520696e20626173653634", result);
    }

    @Test
    void convertBase64toHexUppercase() {
        //Given
        String base64String = "c3RyaW5nYSBkaSB0ZXN0IGRhIGNvbnZlcnRpcmUgaW4gYmFzZTY0";

        //When
        String result = FileUtils.convertBase64toHexUppercase(base64String);

        //Then
        assertEquals("737472696E6761206469207465737420646120636F6E7665727469726520696E20626173653634", result);
    }
}
