package it.pagopa.pn.commons.db.campaign;

import it.pagopa.pn.commons.db.campaign.entity.CampaignChannel;
import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;
import it.pagopa.pn.commons.db.campaign.entity.CampaignStatus;
import it.pagopa.pn.commons.db.campaign.entity.WorkflowEntity;
import it.pagopa.pn.commons.exceptions.InvalidCampaignException;
import it.pagopa.pn.commons.exceptions.PnCampaignNotFoundException;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampaignServiceCachedProviderTest {

    @Test
    void shouldGetBySenderId() {
        CampaignEntity first = validCampaign("550e8400-e29b-41d4-a716-446655440001", "camp-001");
        CampaignEntity second = validCampaign("550e8400-e29b-41d4-a716-446655440001", "camp-002");
        CampaignEntity third = validCampaign("550e8400-e29b-41d4-a716-446655440002", "camp-003");

        CampaignServiceCachedProvider provider = createProviderWithCampaigns(List.of(first, second, third));

        List<CampaignEntity> result = provider.getBySenderId("550e8400-e29b-41d4-a716-446655440001");
        assertEquals(List.of(first, second), result);
    }

    @Test
    void shouldGetByCampaignIdAndSenderId() {
        CampaignEntity campaign = validCampaign("550e8400-e29b-41d4-a716-446655440001", "camp-001");
        CampaignServiceCachedProvider provider = createProviderWithCampaigns(List.of(campaign));

        CampaignEntity result = provider.getByCampaignIdAndSenderId("camp-001", "550e8400-e29b-41d4-a716-446655440001");
        assertEquals(campaign, result);
    }

    @Test
    void shouldThrowWhenCampaignNotFound() {
        CampaignEntity campaign = validCampaign("550e8400-e29b-41d4-a716-446655440001", "camp-001");
        CampaignServiceCachedProvider provider = createProviderWithCampaigns(List.of(campaign));

        assertThrows(PnCampaignNotFoundException.class,
                () -> provider.getByCampaignIdAndSenderId("camp-404", "550e8400-e29b-41d4-a716-446655440001"));
    }

    @Test
    void shouldThrowWhenCampaignIsInvalidInCache() {
        CampaignEntity invalidCampaign = validCampaign("550e8400-e29b-41d4-a716-446655440001", "camp-001");
        invalidCampaign.setWorkflow(null);

        assertThrows(InvalidCampaignException.class, () -> createProviderWithCampaigns(List.of(invalidCampaign)));
    }

    @Test
    void shouldThrowForBlankTableName() {
        DynamoDbEnhancedClient client = Mockito.mock(DynamoDbEnhancedClient.class);
        assertThrows(IllegalArgumentException.class, () -> new CampaignServiceCachedProvider(client, " "));
    }

    @Test
    void shouldThrowForNullTableName() {
        DynamoDbEnhancedClient client = Mockito.mock(DynamoDbEnhancedClient.class);
        assertThrows(IllegalArgumentException.class, () -> new CampaignServiceCachedProvider(client, null));
    }

    @Test
    void shouldThrowForNullClient() {
        assertThrows(NullPointerException.class, () -> new CampaignServiceCachedProvider(null, "Campaigns"));
    }

    @SuppressWarnings("unchecked")
    private CampaignServiceCachedProvider createProviderWithCampaigns(List<CampaignEntity> campaigns) {
        DynamoDbEnhancedClient client = Mockito.mock(DynamoDbEnhancedClient.class);
        DynamoDbTable<CampaignEntity> table = Mockito.mock(DynamoDbTable.class);
        PageIterable<CampaignEntity> pageIterable = Mockito.mock(PageIterable.class);
        SdkIterable<CampaignEntity> sdkIterable = campaigns::iterator;

        Mockito.when(client.table(Mockito.eq("Campaigns"), ArgumentMatchers.<TableSchema<CampaignEntity>>any()))
                .thenReturn(table);
        Mockito.when(table.scan()).thenReturn(pageIterable);
        Mockito.when(pageIterable.items()).thenReturn(sdkIterable);

        return new CampaignServiceCachedProvider(client, "Campaigns");
    }

    private CampaignEntity validCampaign(String senderId, String campaignId) {
        WorkflowEntity workflow = WorkflowEntity.builder()
                .channel(CampaignChannel.IO)
                .recipientType(Set.of(RecipientTypeInt.PF))
                .timeout(Duration.ofHours(2))
                .includeAttachment(false)
                .build();

        return CampaignEntity.builder()
                .senderId(senderId)
                .campaignId(campaignId)
                .title("Campaign")
                .descriptionScope("Description")
                .startDate(Instant.parse("2026-01-01T00:00:00Z"))
                .endDate(Instant.parse("2026-12-31T23:59:59Z"))
                .status(CampaignStatus.IN_PROGRESS)
                .serviceId("service-001")
                .serviceName("Service")
                .sensitiveContent(false)
                .stopOnViewed(false)
                .workflow(List.of(workflow))
                .build();
    }
}
