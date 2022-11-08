package it.pagopa.pn.commons.pnclients.recipientschallenge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipientsChallengeImplTest {

    @Test
    void getSecret() {
        RecipientsChallengeImpl recipientsChallenge = new RecipientsChallengeImpl();
        String res = recipientsChallenge.getSecret("taxid");
        assertEquals("taxid-secret", res);
    }
}