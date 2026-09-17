package it.pagopa.pn.commons.db.campaign.converter;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;

@Slf4j
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
        if (attributeValue == null) {
            return null;
        }

        if (attributeValue.s() != null && !attributeValue.s().isBlank()) {
            try {
                return Duration.parse(attributeValue.s().trim());
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot parse duration string: " + attributeValue.s(), ex);
            }
        }

        if (attributeValue.n() != null && !attributeValue.n().isBlank()) {
            try {
                return Duration.ofSeconds(Long.parseLong(attributeValue.n().trim()));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Cannot parse duration number: " + attributeValue.n(), ex);
            }
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
