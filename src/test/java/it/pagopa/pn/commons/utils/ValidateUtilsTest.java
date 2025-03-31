package it.pagopa.pn.commons.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.pagopa.pn.commons.configs.TaxIdInBlackListParameterConsumer;
import it.pagopa.pn.commons.configs.TaxIdInWhiteListParameterConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateUtilsTest {

    private ValidateUtils validateUtils;

    @Mock
    TaxIdInWhiteListParameterConsumer taxIdInWhiteListParameterConsumer;

    @Mock
    TaxIdInBlackListParameterConsumer taxIdInBlackListParameterConsumer;


    /**
     * Method under test: {@link ValidateUtils#validate(String)}
     */

    @BeforeEach
    void setup() {
        validateUtils = new ValidateUtils( taxIdInWhiteListParameterConsumer, taxIdInBlackListParameterConsumer);
    }

    @Test
    void testValidateCfOmocodici(){
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        assertTrue(validateUtils.validate("MRNLCC00A01H50MJ"));
        assertTrue(validateUtils.validate("MRNLCU00A01H50MB"));
        assertTrue(validateUtils.validate("BRNLCU00A01H50MJ"));
        assertTrue(validateUtils.validate("MRNLCU00A01H90MJ"));
    }

    @Test
    void testValidateCfFalse() {
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        assertFalse(validateUtils.validate("@@@"));
        assertFalse(validateUtils.validate("@@@@@@@@@@@@@@@@"));
        assertFalse(validateUtils.validate("@@@@@@@@@@@"));
        assertFalse(validateUtils.validate("MRORSS00A00A000V"));
        assertFalse(validateUtils.validate("MRORSS00A+0A000V"));
        assertFalse(validateUtils.validate("00000+00000"));
        assertFalse(validateUtils.validate("FRNSST75D50A717M"));
        assertTrue(validateUtils.validate("FRNSST75D50A717N"));
        assertTrue(validateUtils.validate("MRO rSs 00a00 A000U"));
        assertTrue(validateUtils.validate("KJWMFE88C50E205S"));
        assertTrue(validateUtils.validate("GNNTIS14L02X498V"));
        assertTrue(validateUtils.validate("JKNXZK26E16Y097M"));

    }
    @Test
    void validateIva(){
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        assertTrue(validateUtils.validate("00000000000"));
        assertTrue(validateUtils.validate("44444444440"));
        assertTrue(validateUtils.validate("12345678903"));
        assertTrue(validateUtils.validate("74700694370"));
        assertTrue(validateUtils.validate("57636564049"));
        assertTrue(validateUtils.validate("19258897628"));
        assertTrue(validateUtils.validate("08882740981"));
        assertTrue(validateUtils.validate("4730 9842  806"));
    }

    @Test
    void validateInWitheList() {
        validateUtils = new ValidateUtils(taxIdInWhiteListParameterConsumer, taxIdInBlackListParameterConsumer);
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( true );
        assertTrue(validateUtils.validate("AAAAEEEEEEEDDD"));
    }

    @Test
    void taxIdIsInWhiteList() {
        String fakeTaxId = "MRNLCC00A01H50MJ";
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        assertFalse(validateUtils.taxIdIsInWhiteList(fakeTaxId));
    }

    @Test
    void validateWithSkipCheckInWhiteList() {
        String fakeTaxId = "MRNLCC00A01H50MJ";
        assertTrue(validateUtils.validate(fakeTaxId, true));
        Mockito.verifyNoInteractions( taxIdInWhiteListParameterConsumer );
    }


    @Test
    void validateWithCheckInWhiteList() {
        String fakeTaxId = "MRNLCC00A01H50MJ";
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        assertTrue(validateUtils.validate(fakeTaxId, true, false));
    }

    @Test
    void validateWithCheckInWhiteList2() {
        String fakeTaxId = "MRNLCC00A01H50MA";
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        assertFalse(validateUtils.validate(fakeTaxId, true, false));
    }

    @Test
    void validateWithEmptyCF() {
        String fakeTaxId = "";
        assertFalse(validateUtils.validate(fakeTaxId, true, false, true));
    }

    @Test
    void validateWithWhitelist() {
        String fakeTaxId = "MRNLCC00A01H50MJ";
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( true );
        assertTrue(validateUtils.validate(fakeTaxId, true, false, true));
    }

    @Test
    void validateWithSkipBlackList() {
        String fakeTaxId = "MRNLCC00A01H50MJ";
        assertTrue(validateUtils.validate(fakeTaxId, true, false, true));
    }

    @Test
    void validateWithSkipBlackListNumeric() {
        String fakeTaxId = "61283750612";
        assertTrue(validateUtils.validate(fakeTaxId, false, false, true));
    }

    @Test
    void validateWithSkipBlackListNumericWithPf() {
        String fakeTaxId = "61283750612";
        assertFalse(validateUtils.validate(fakeTaxId, true, false, true));
    }

    @Test
    void validateWithCheckInBlackList() {
        String fakeTaxId = "MRNLCC00A01H50MJ";
        Mockito.when( taxIdInBlackListParameterConsumer.isInBlackList( Mockito.anyString() ) ).thenReturn( true );
        assertFalse(validateUtils.validate(fakeTaxId, true, true, false));
    }

    @Test
    void validateWithCheckInBlackList2() {
        String fakeTaxId = "BRNBRN80A01H501V";
        Mockito.when( taxIdInBlackListParameterConsumer.isInBlackList( Mockito.anyString() ) ).thenReturn( false );
        assertTrue(validateUtils.validate(fakeTaxId, true, true, false));
    }

    @Test
    void validateWithCheckInWhiteAndBlackList2() {
        String fakeTaxId = "MRNLCC00A01H50MA";
        Mockito.when( taxIdInWhiteListParameterConsumer.isInWhiteList( Mockito.anyString() ) ).thenReturn( false );
        Mockito.when( taxIdInBlackListParameterConsumer.isInBlackList( Mockito.anyString() ) ).thenReturn( false );
        assertFalse(validateUtils.validate(fakeTaxId, true, false, false));
    }
}

