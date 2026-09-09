package it.pagopa.pn.commons.db.campaign.converter;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DurationAsStringAttributeConverterTest {

    private final DurationAsStringAttributeConverter converter = new DurationAsStringAttributeConverter();

    @Test
    void shouldTransformFromDurationToStringAttribute() {
        AttributeValue result = converter.transformFrom(Duration.ofHours(3));
        assertEquals("PT3H", result.s());
    }

    @Test
    void shouldTransformFromNullDurationToNullAttribute() {
        AttributeValue result = converter.transformFrom(null);
        assertTrue(result.nul());
    }

    @Test
    void shouldTransformToDurationFromStringAttribute() {
        Duration result = converter.transformTo(AttributeValue.builder().s("PT45M").build());
        assertEquals(Duration.ofMinutes(45), result);
    }

    @Test
    void shouldTransformToNullForEmptyOrNullAttribute() {
        assertNull(converter.transformTo(null));
        assertNull(converter.transformTo(AttributeValue.builder().s("").build()));
        assertNull(converter.transformTo(AttributeValue.builder().nul(true).build()));
    }

    @Test
    void shouldThrowForMalformedDuration() {
        assertThrows(DateTimeParseException.class, () -> converter.transformTo(AttributeValue.builder().s("abc").build()));
    }

    @Test
    void shouldExposeStringAttributeValueType() {
        assertEquals(AttributeValueType.S, converter.attributeValueType());
    }
}
