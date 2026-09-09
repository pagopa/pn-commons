package it.pagopa.pn.commons.db.campaign.entity;

import it.pagopa.pn.commons.db.campaign.converter.DurationAsStringAttributeConverter;
import it.pagopa.pn.commons.utils.qr.models.RecipientTypeInt;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;

import java.time.Duration;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@DynamoDbBean
public class WorkflowEntity {
    @Getter(onMethod=@__({@DynamoDbAttribute("channel")}))
    private CampaignChannel channel;

    @Getter(onMethod=@__({@DynamoDbAttribute("recipientType")}))
    private Set<RecipientTypeInt> recipientType;

    @Getter(onMethod=@__({@DynamoDbAttribute("timeout"), @DynamoDbConvertedBy(DurationAsStringAttributeConverter.class)}))
    private Duration timeout;

    @Getter(onMethod=@__({@DynamoDbAttribute("desiredFeedback")}))
    private Set<DesiredFeedback> desiredFeedback;

    @Getter(onMethod=@__({@DynamoDbAttribute("includeAttachment")}))
    private Boolean includeAttachment;
}
