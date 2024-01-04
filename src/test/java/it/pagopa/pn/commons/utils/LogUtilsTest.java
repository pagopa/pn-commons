package it.pagopa.pn.commons.utils;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
class LogUtilsTest {

    @ParameterizedTest
    @CsvSource(
        value = { "email@email.it, e***l@email.it", "em@email.it, ***@email.it", "test.email@domain.com, t********l@domain.com", "NULL, null" }, 
        nullValues={"NULL"}
    )
    void maskEmailAddress(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskEmailAddress(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "notValidEmail", "not.valid.email", "@domain.com", "2017-02-03T10:37:30.00Z", "123456789" })
    void givenMalformedEmailShouldThrowIllegalArgumentException(String email) {
        // Given (in ValueSource)

        // When / Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> LogUtils.maskEmailAddress(email));
    }


    @ParameterizedTest
    @CsvSource(value ={ "333123456, 3*****456", "NULL, null" }, nullValues={"NULL"})
    void maskNumber(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskNumber(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }



    @ParameterizedTest
    @CsvSource(value ={ "una qualche stringa lunga, u*********************nga", "un, ***", "NULL, null" }, nullValues={"NULL"})
    void maskGeneric(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskGeneric(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }



    @ParameterizedTest
    @CsvSource(value ={ "CSRGGL44L13H501E, C************01E", "NULL, null" }, nullValues={"NULL"})
    void maskTaxId(String str, String expected) {
        //Given

        //When
        String result = LogUtils.maskTaxId(str);

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void maskString() {
        //Given
        String str = "una qualche stringa lunga";

        //When
        String result = LogUtils.maskString(str, 1, 5, '^');

        //Then
        Assertions.assertNotEquals( str, result);
        Assertions.assertEquals("u^^^^ualche stringa lunga", result);
    }

    @Test
    void getMessageWithSafeUrl() {
        //Given
        String fileName = "filename.pdf";
        String url = "https://fakeurlfordownload?token=faketoken";

        //When
        String messageResult = LogUtils.createAuditLogMessageForDownloadDocument( fileName, url, null );

        //Then
        Assertions.assertNotNull( messageResult );
        Assertions.assertEquals( "filename=filename.pdf, url=https://fakeurlfordownload", messageResult );
    }

    @Test
    void getMessageWithRetryAfter() {
        //Given
        String fileName = "filename.pdf";
        String retryAfter = "3600";

        //When
        String messageResult = LogUtils.createAuditLogMessageForDownloadDocument( fileName, null, retryAfter );

        //Then
        Assertions.assertNotNull( messageResult );
        Assertions.assertEquals( "filename=filename.pdf, retryAfter=3600", messageResult );
    }

}
