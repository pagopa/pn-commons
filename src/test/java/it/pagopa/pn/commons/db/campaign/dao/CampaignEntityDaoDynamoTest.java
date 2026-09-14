package it.pagopa.pn.commons.db.campaign.dao;

import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

class CampaignEntityDaoDynamoTest {

    private DynamoDbTable<CampaignEntity> table;
    private CampaignEntityDaoDynamo campaignEntityDaoDynamo;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
        table = mock(DynamoDbTable.class);
        when(dynamoDbEnhancedClient.table(eq("Campaigns"), ArgumentMatchers.<TableSchema<CampaignEntity>>any()))
                .thenReturn(table);
        campaignEntityDaoDynamo = new CampaignEntityDaoDynamo(dynamoDbEnhancedClient, "Campaigns");
    }

    @Test
    @SuppressWarnings("unchecked")
    void scanAllShouldReturnAllItems() {
        PageIterable<CampaignEntity> pageIterable = mock(PageIterable.class);
        List<CampaignEntity> campaigns = List.of(
                CampaignEntity.builder().senderId("550e8400-e29b-41d4-a716-446655440001").campaignId("camp-001").build(),
                CampaignEntity.builder().senderId("550e8400-e29b-41d4-a716-446655440002").campaignId("camp-002").build()
        );
        SdkIterable<CampaignEntity> sdkIterable = campaigns::iterator;

        when(table.scan()).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);

        assertEquals(campaigns, campaignEntityDaoDynamo.scanAll());
    }

    @Test
    void putIfAbsentShouldThrowUnsupportedOperationException() {
        CampaignEntity campaign = CampaignEntity.builder().build();
        assertThrows(UnsupportedOperationException.class, () -> campaignEntityDaoDynamo.putIfAbsent(campaign));
    }
}

