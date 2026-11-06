package it.pagopa.pn.commons.db.campaign;

import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;

import java.util.List;

public interface CampaignProvider {

    List<CampaignEntity> getBySenderId(String senderId);

    CampaignEntity getByCampaignIdAndSenderId(
            String campaignId, String senderId);
}