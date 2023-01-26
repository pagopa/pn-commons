package it.pagopa.pn.commons.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateUtilsTest {
    /**
     * Method under test: {@link ValidateUtils#validate(String)}
     */
    @Test
    void testValidateCf() {
        assertTrue(ValidateUtils.validate("FRNSST75D50A717N"));

        Assertions.assertFalse(ValidateUtils.validate(""));
        Assertions.assertFalse(ValidateUtils.validate("@@@"));
        Assertions.assertFalse(ValidateUtils.validate("@@@@@@@@@@@@@@@@"));
        Assertions.assertFalse(ValidateUtils.validate("@@@@@@@@@@@"));
        Assertions.assertFalse(ValidateUtils.validate("MRORSS00A00A000V"));
        Assertions.assertFalse(ValidateUtils.validate("MRORSS00A+0A000V"));
        Assertions.assertFalse(ValidateUtils.validate("00000+00000"));

        assertTrue(ValidateUtils.validate("MRO rSs 00a00 A000U"));
        assertTrue(ValidateUtils.validate("KJWMFE88C50E205S"));
        assertTrue(ValidateUtils.validate("GNNTIS14L02X498V"));
        assertTrue(ValidateUtils.validate("JKNXZK26E16Y097M"));

        assertTrue(ValidateUtils.validate("00000000000"));
        assertTrue(ValidateUtils.validate("44444444440"));
        assertTrue(ValidateUtils.validate("12345678903"));
        assertTrue(ValidateUtils.validate("74700694370"));
        assertTrue(ValidateUtils.validate("57636564049"));
        assertTrue(ValidateUtils.validate("19258897628"));
        assertTrue(ValidateUtils.validate("08882740981"));
        assertTrue(ValidateUtils.validate("4730 9842  806"));

    }

}

