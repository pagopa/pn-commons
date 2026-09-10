package it.pagopa.pn.commons.db.campaign.utils;

import it.pagopa.pn.commons.db.campaign.entity.CampaignChannel;
import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;
import it.pagopa.pn.commons.db.campaign.entity.CampaignStatus;
import it.pagopa.pn.commons.db.campaign.entity.WorkflowEntity;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignEntityValidatorTest {

    @Test
    void shouldReturnTrueForValidCampaign() {
        CampaignEntity campaign = validCampaign();
        assertTrue(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForNullCampaign() {
        assertFalse(CampaignEntityValidator.isValid(null));
    }

    @Test
    void shouldReturnFalseForInvalidSenderId() {
        CampaignEntity campaign = validCampaign();
        campaign.setSenderId("invalid-uuid");
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForInvalidWorkflowStep() {
        CampaignEntity campaign = validCampaign();
        campaign.setWorkflow(List.of(WorkflowEntity.builder()
                .channel(CampaignChannel.IO)
                .recipientType(Set.of())
                .timeout(Duration.ofHours(1))
                .includeAttachment(false)
                .build()));
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    private CampaignEntity validCampaign() {
        WorkflowEntity workflow = WorkflowEntity.builder()
                .channel(CampaignChannel.IO)
                .recipientType(Set.of(RecipientTypeInt.PF))
                .timeout(Duration.ofHours(2))
                .includeAttachment(false)
                .build();

        return CampaignEntity.builder()
                .senderId("550e8400-e29b-41d4-a716-446655440001")
                .campaignId("camp-001")
                .title("Campaign 1")
                .descriptionScope("Scope")
                .startDate(Instant.parse("2026-01-01T00:00:00Z"))
                .endDate(Instant.parse("2026-12-31T23:59:59Z"))
                .status(CampaignStatus.IN_PROGRESS)
                .serviceId("service-001")
                .serviceName("Service 1")
                .sensitiveContent(false)
                .stopOnViewed(false)
                .workflow(List.of(workflow))
                .build();
    }
}

