package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LogUtilsTest {

    @ParameterizedTest
    @CsvSource(value ={ "email@email.it, e***l@email.it", "em@email.it, ***@email.it", "NULL, null" }, nullValues={"NULL"})
    void maskEmailAddress(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskEmailAddress(str);

        //Then
        Assertions.assertNotEquals( str, result);
        assertEquals(expected, result);
    }


    @ParameterizedTest
    @CsvSource(value ={ "333123456, 3*****456", "NULL, null" }, nullValues={"NULL"})
    void maskNumber(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskNumber(str);

        //Then
        Assertions.assertNotEquals( str, result);
        assertEquals(expected, result);
    }



    @ParameterizedTest
    @CsvSource(value ={ "una qualche stringa lunga, u*********************nga", "un, ***", "NULL, null" }, nullValues={"NULL"})
    void maskGeneric(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskGeneric(str);

        //Then
        Assertions.assertNotEquals( str, result);
        assertEquals(expected, result);
    }



    @ParameterizedTest
    @CsvSource(value ={ "CSRGGL44L13H501E, C************01E", "NULL, null" }, nullValues={"NULL"})
    void maskTaxId(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskTaxId(str);

        //Then
        Assertions.assertNotEquals( str, result);
        assertEquals(expected, result);
    }


    @Test
    void maskString() {
        //Given
        String str = "una qualche stringa lunga";

        //When
        String result = LogUtils.maskString(str, 1, 5, '^');

        //Then
        Assertions.assertNotEquals( str, result);
        assertEquals("u^^^^ualche stringa lunga", result);
    }

}