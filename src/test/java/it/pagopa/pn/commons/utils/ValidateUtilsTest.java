package it.pagopa.pn.commons.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateUtilsTest {
    /**
     * Method under test: {@link ValidateUtils#validate(String)}
     */

    @Test
    void testValidateCfOmocodici(){
        assertTrue(ValidateUtils.validate("MRNLCC00A01H50MJ"));
        assertTrue(ValidateUtils.validate("MRNLCU00A01H50MB"));
        assertTrue(ValidateUtils.validate("BRNLCU00A01H50MJ"));
        assertTrue(ValidateUtils.validate("MRNLCU00A01H90MJ"));
    }

    @Test
    void testValidateCfFalse() {
        assertFalse(ValidateUtils.validate("@@@"));
        assertFalse(ValidateUtils.validate("@@@@@@@@@@@@@@@@"));
        assertFalse(ValidateUtils.validate("@@@@@@@@@@@"));
        assertFalse(ValidateUtils.validate("MRORSS00A00A000V"));
        assertFalse(ValidateUtils.validate("MRORSS00A+0A000V"));
        assertFalse(ValidateUtils.validate("00000+00000"));
        assertFalse(ValidateUtils.validate("FRNSST75D50A717M"));
        assertTrue(ValidateUtils.validate("FRNSST75D50A717N"));
        assertTrue(ValidateUtils.validate("MRO rSs 00a00 A000U"));
        assertTrue(ValidateUtils.validate("KJWMFE88C50E205S"));
        assertTrue(ValidateUtils.validate("GNNTIS14L02X498V"));
        assertTrue(ValidateUtils.validate("JKNXZK26E16Y097M"));

    }
    @Test
    void validateIva(){
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

