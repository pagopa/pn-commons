package it.pagopa.pn.commons.db.campaign;

import it.pagopa.pn.commons.db.campaign.dao.CampaignDao;
import it.pagopa.pn.commons.db.campaign.dao.CampaignEntityDaoDynamo;
import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;
import it.pagopa.pn.commons.db.campaign.utils.CampaignEntityValidator;
import it.pagopa.pn.commons.exceptions.InvalidCampaignException;
import it.pagopa.pn.commons.exceptions.PnCampaignNotFoundException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.util.List;
import java.util.Objects;

/**
 * Provider class for campaign service. This class is designed to be instantiated
 * by client microservices without Spring dependency injection, ensuring compatibility
 * across different Spring Boot versions.
 */
@Slf4j
public class CampaignServiceCachedProvider {

    private final List<CampaignEntity> cachedCampaigns;

    public CampaignServiceCachedProvider(DynamoDbEnhancedClient client, String tableName) {
        Objects.requireNonNull(client, "DynamoDbEnhancedClient cannot be null");
        if (tableName != null && !tableName.isBlank()) {
            log.info("Initializing CampaignServiceCachedProvider with table: {}", tableName);
            CampaignDao campaignDao = new CampaignEntityDaoDynamo(client, tableName);
            this.cachedCampaigns = campaignDao.scanAll();
            log.info("Loaded {} campaigns into cache", cachedCampaigns.size());
            validateCampaigns();
        } else {
            throw new IllegalArgumentException("tableName cannot be null or blank");
        }
    }

    private void validateCampaigns() {
        for (CampaignEntity campaign : cachedCampaigns) {
            if(!CampaignEntityValidator.isValid(campaign)) {
                log.warn("Invalid campaign found in cache: {}", campaign);
                throw new InvalidCampaignException(campaign.getSenderId(), campaign.getCampaignId());
            }
        }
    }

    public List<CampaignEntity> getBySenderId(String senderId) {
        return cachedCampaigns.stream()
                .filter(c -> c.getSenderId().equals(senderId))
                .toList();
    }

    public CampaignEntity getByCampaignIdAndSenderId(String campaignId, String senderId) {
        return cachedCampaigns.stream()
                .filter(c -> c.getSenderId().equals(senderId) && c.getCampaignId().equals(campaignId))
                .findFirst()
                .orElseThrow(() -> new PnCampaignNotFoundException(
                        String.format("Campaign with campaignId=%s and senderId=%s not found", campaignId, senderId)
                ));
    }
}
