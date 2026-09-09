package it.pagopa.pn.commons.exceptions;

public class InvalidCampaignException extends PnInternalException {
    public InvalidCampaignException(String senderId, String campaignId) {
        super(String.format("Campaign with campaignId=%s and senderId=%s is invalid", campaignId, senderId), "INVALID_CAMPAIGN");
    }
}
