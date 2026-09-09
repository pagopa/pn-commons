package it.pagopa.pn.commons.db.campaign.converter;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;

public class DurationAsStringAttributeConverter implements AttributeConverter<Duration> {

    @Override
    public AttributeValue transformFrom(Duration input) {
        if (input == null) {
            return AttributeValue.builder().nul(true).build();
        }
        return AttributeValue.builder().s(input.toString()).build();
    }

    @Override
    public Duration transformTo(AttributeValue attributeValue) {
        if (attributeValue != null && attributeValue.s() != null && !attributeValue.s().isBlank()) {
            return Duration.parse(attributeValue.s());
        }
        return null;
    }

    @Override
    public EnhancedType<Duration> type() {
        return EnhancedType.of(Duration.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}

