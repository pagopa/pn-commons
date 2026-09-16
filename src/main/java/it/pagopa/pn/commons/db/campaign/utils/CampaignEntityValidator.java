package it.pagopa.pn.commons.db.campaign.utils;


import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;
import it.pagopa.pn.commons.db.campaign.entity.WorkflowEntity;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CampaignEntityValidator {
    private CampaignEntityValidator() {
        // private constructor to prevent instantiation
    }

    public static boolean isValid(CampaignEntity campaign) {
        return Objects.nonNull(campaign)
                && StringUtils.hasText(campaign.getCampaignId())
                && isValidSenderId(campaign.getSenderId())
                && StringUtils.hasText(campaign.getTitle())
                && StringUtils.hasText(campaign.getDescriptionScope())
                && Objects.nonNull(campaign.getStartDate())
                && Objects.nonNull(campaign.getEndDate())
                && Objects.nonNull(campaign.getStatus())
                && StringUtils.hasText(campaign.getServiceId())
                && hasValidWorkflow(campaign.getWorkflow());
    }

    private static boolean isValidSenderId(String senderId) {
        if (!StringUtils.hasText(senderId)) {
            return false;
        }

        try {
            UUID.fromString(senderId);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static boolean hasValidWorkflow(List<WorkflowEntity> workflow) {
        return Objects.nonNull(workflow)
                && workflow.stream().allMatch(CampaignEntityValidator::hasValidWorkflowStep);
    }

    private static boolean hasValidWorkflowStep(WorkflowEntity workflowStep) {
        return Objects.nonNull(workflowStep)
                && Objects.nonNull(workflowStep.getChannel())
                && Objects.nonNull(workflowStep.getRecipientType())
                && !workflowStep.getRecipientType().isEmpty()
                && workflowStep.getRecipientType().stream().allMatch(Objects::nonNull);
    }
}
