package it.pagopa.pn.commons.db.campaign.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContactEntity {

    @Getter(onMethod=@__({@DynamoDbAttribute("email")}))
    private String email;

    @Getter(onMethod=@__({@DynamoDbAttribute("pec")}))
    private String pec;

    @Getter(onMethod=@__({@DynamoDbAttribute("phone")}))
    private String phone;
}