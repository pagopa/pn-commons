package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MimeTypesUtilsTest {

    @Test
    void getDefaultExt() {
        //Given
        String mime = "application/pdf";

        //When
        String result = MimeTypesUtils.getDefaultExt(mime);

        //Then
        assertEquals("pdf", result);
    }
}