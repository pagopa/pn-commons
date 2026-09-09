package it.pagopa.pn.commons.db.campaign.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.List;


@DynamoDbBean
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CampaignEntity {
    public static final String COL_SENDER_ID = "senderId";
    public static final String COL_CAMPAIGN_ID = "campaignId";
    
    @Getter(onMethod=@__({@DynamoDbPartitionKey, @DynamoDbAttribute(COL_SENDER_ID)}))
    private String senderId;
    
    @Getter(onMethod=@__({@DynamoDbSortKey, @DynamoDbAttribute(COL_CAMPAIGN_ID)}))
    private String campaignId;

    @Getter(onMethod=@__({@DynamoDbAttribute("title")}))
    private String title;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("descriptionScope")}))
    private String descriptionScope;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("startDate")}))
    private Instant startDate;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("endDate")}))
    private Instant endDate;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("status")}))
    private CampaignStatus status;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("senderContact")}))
    private String senderContact;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("serviceId")}))
    private String serviceId;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("serviceName")}))
    private String serviceName;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("sensitiveContent")}))
    private Boolean sensitiveContent;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("stopOnViewed")}))
    private Boolean stopOnViewed;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("taxonomyCode")}))
    private String taxonomyCode;
    
    @Getter(onMethod=@__({@DynamoDbAttribute("workflow")}))
    private List<WorkflowEntity> workflow;
}

