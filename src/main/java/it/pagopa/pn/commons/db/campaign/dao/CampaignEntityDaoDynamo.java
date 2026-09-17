package it.pagopa.pn.commons.db.campaign.dao;

import it.pagopa.pn.commons.abstractions.impl.AbstractDynamoKeyValueStore;
import it.pagopa.pn.commons.db.campaign.entity.CampaignEntity;
import it.pagopa.pn.commons.exceptions.PnIdConflictException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Slf4j
public class CampaignEntityDaoDynamo extends AbstractDynamoKeyValueStore<CampaignEntity> implements CampaignDao {

    public CampaignEntityDaoDynamo(DynamoDbEnhancedClient dynamoDbEnhancedClient, String tableName) {
        super(dynamoDbEnhancedClient.table(tableName, TableSchema.fromClass(CampaignEntity.class)));
    }

    @Override
    public void putIfAbsent(CampaignEntity value) throws PnIdConflictException {
        throw new UnsupportedOperationException("method put if absent not supported");
    }

    @Override
    public List<CampaignEntity> scanAll() {
        return table.scan().items().stream().toList();
    }
}
