package it.pagopa.pn.commons.db.campaign.utils;

import it.pagopa.pn.commons.db.campaign.entity.*;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
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

    @Test
    void shouldReturnFalseForBlankCampaignId() {
        CampaignEntity campaign = validCampaign();
        campaign.setCampaignId(" ");
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForBlankTitle() {
        CampaignEntity campaign = validCampaign();
        campaign.setTitle(" ");
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForBlankDescriptionScope() {
        CampaignEntity campaign = validCampaign();
        campaign.setDescriptionScope(" ");
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForNullStartDate() {
        CampaignEntity campaign = validCampaign();
        campaign.setStartDate(null);
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForNullEndDate() {
        CampaignEntity campaign = validCampaign();
        campaign.setEndDate(null);
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForNullStatus() {
        CampaignEntity campaign = validCampaign();
        campaign.setStatus(null);
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForBlankServiceId() {
        CampaignEntity campaign = validCampaign();
        campaign.setServiceId(" ");
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForNullWorkflow() {
        CampaignEntity campaign = validCampaign();
        campaign.setWorkflow(null);
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnTrueForEmptyWorkflow() {
        CampaignEntity campaign = validCampaign();
        campaign.setWorkflow(List.of());
        assertTrue(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldReturnFalseForWorkflowStepWithNullRecipientTypeElement() {
        CampaignEntity campaign = validCampaign();
        Set<RecipientTypeInt> recipientTypes = new HashSet<>(Arrays.asList(RecipientTypeInt.PF, null));
        WorkflowEntity step = WorkflowEntity.builder()
                .channel(CampaignChannel.IO)
                .recipientType(recipientTypes)
                .timeout(Duration.ofMinutes(5))
                .includeAttachment(true)
                .build();
        campaign.setWorkflow(List.of(step));
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void hasValidWorkflowStepShouldReturnFalseForNullStep() throws Exception {
        assertFalse(invokeHasValidWorkflowStep(null));
    }

    @Test
    void hasValidWorkflowStepShouldReturnFalseForNullChannel() throws Exception {
        WorkflowEntity step = WorkflowEntity.builder()
                .channel(null)
                .recipientType(Set.of(RecipientTypeInt.PF))
                .build();
        assertFalse(invokeHasValidWorkflowStep(step));
    }

    @Test
    void hasValidWorkflowStepShouldReturnFalseForNullRecipientType() throws Exception {
        WorkflowEntity step = WorkflowEntity.builder()
                .channel(CampaignChannel.EMAIL)
                .recipientType(null)
                .build();
        assertFalse(invokeHasValidWorkflowStep(step));
    }

    @Test
    void hasValidWorkflowStepShouldReturnFalseForEmptyRecipientType() throws Exception {
        WorkflowEntity step = WorkflowEntity.builder()
                .channel(CampaignChannel.EMAIL)
                .recipientType(Set.of())
                .build();
        assertFalse(invokeHasValidWorkflowStep(step));
    }

    @Test
    void hasValidWorkflowStepShouldReturnTrueForValidStep() throws Exception {
        WorkflowEntity step = WorkflowEntity.builder()
                .channel(CampaignChannel.PEC)
                .recipientType(Set.of(RecipientTypeInt.PG))
                .desiredFeedback(Set.of(DesiredFeedback.RECEIVED))
                .timeout(Duration.ofMinutes(3))
                .build();
        assertTrue(invokeHasValidWorkflowStep(step));
    }

    @Test
    void shouldRejectNullOrEmptyContacts() {
        CampaignEntity campaign = validCampaign();

        campaign.setContacts(null);
        assertFalse(CampaignEntityValidator.isValid(campaign));

        campaign.setContacts(List.of());
        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldAcceptEachContactTypeOnItsOwn() {
        List<ContactEntity> contacts = List.of(
                ContactEntity.builder().email("info@example.org").build(),
                ContactEntity.builder().pec("pec@example.org").build(),
                ContactEntity.builder().phone("+390911234567").build()
        );

        for (ContactEntity contact : contacts) {
            CampaignEntity campaign = validCampaign();
            campaign.setContacts(List.of(contact));

            assertTrue(CampaignEntityValidator.isValid(campaign));
        }
    }

    @Test
    void shouldAcceptMultipleValidContacts() {
        CampaignEntity campaign = validCampaign();
        campaign.setContacts(List.of(
                ContactEntity.builder().email("info@example.org").build(),
                ContactEntity.builder().phone("+390911234567").build()
        ));

        assertTrue(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldRejectContactWithoutRecapiti() {
        CampaignEntity campaign = validCampaign();
        campaign.setContacts(List.of(ContactEntity.builder().build()));

        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldRejectContactWithOnlyBlankRecapiti() {
        CampaignEntity campaign = validCampaign();
        campaign.setContacts(List.of(ContactEntity.builder()
                .email(" ")
                .pec("")
                .phone("\t")
                .build()));

        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldRejectNullContactInList() {
        CampaignEntity campaign = validCampaign();
        campaign.setContacts(Arrays.asList(
                ContactEntity.builder().email("info@example.org").build(),
                null
        ));

        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    @Test
    void shouldRejectListContainingAnInvalidContact() {
        CampaignEntity campaign = validCampaign();
        campaign.setContacts(List.of(
                ContactEntity.builder().email("info@example.org").build(),
                ContactEntity.builder().build()
        ));

        assertFalse(CampaignEntityValidator.isValid(campaign));
    }

    private CampaignEntity validCampaign() {
        WorkflowEntity workflow = WorkflowEntity.builder()
                .channel(CampaignChannel.IO)
                .recipientType(Set.of(RecipientTypeInt.PF))
                .timeout(Duration.ofHours(2))
                .desiredFeedback(Set.of(DesiredFeedback.READ))
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
                .contacts(List.of(ContactEntity.builder()
                        .email("info@example.org")
                        .build()))
                .build();
    }

    private boolean invokeHasValidWorkflowStep(WorkflowEntity workflowEntity)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = CampaignEntityValidator.class.getDeclaredMethod("hasValidWorkflowStep", WorkflowEntity.class);
        method.setAccessible(true);
        return (boolean) method.invoke(null, workflowEntity);
    }
}
