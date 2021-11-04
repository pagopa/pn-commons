package it.pagopa.pn.commons.pnclients.recipientschallenge;

import org.springframework.stereotype.Component;

@Component
public class RecipientsChallengeImpl implements RecipientsChallenge {
    @Override
    public String getSecret(String taxId) {
        return taxId + "-secret";
    }
}
