package it.pagopa.pn.commons.db.campaign.dao;

import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;

import java.util.List;

public interface CampaignDao {
    List<CampaignEntity> scanAll();
}
