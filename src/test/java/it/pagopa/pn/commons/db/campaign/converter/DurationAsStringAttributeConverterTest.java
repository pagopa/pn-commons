package it.pagopa.pn.commons.db.campaign.converter;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationAsStringAttributeConverterTest {
    private final DurationAsStringAttributeConverter converter = new DurationAsStringAttributeConverter();

    @Test
    void shouldParseIsoDuration() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT24H").build());
        assertEquals(Duration.ofHours(24), duration);
    }

    @Test
    void shouldParseIsoDurationWithSpacesAndLowercase() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("  PT8M ").build());
        assertEquals(Duration.ofMinutes(8), duration);
    }

    @Test
    void shouldParseIsoDurationWithHours() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT12H").build());
        assertEquals(Duration.ofHours(12), duration);
    }

    @Test
    void shouldParseIsoDurationWithMinutes() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT15M").build());
        assertEquals(Duration.ofMinutes(15), duration);
    }

    @Test
    void shouldParseIsoDurationWithSeconds() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT45S").build());
        assertEquals(Duration.ofSeconds(45), duration);
    }

    @Test
    void shouldParseIsoDurationWithDays() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("P2D").build());
        assertEquals(Duration.ofDays(2), duration);
    }

    @Test
    void shouldParseNumericAttributeAsSeconds() {
        Duration duration = converter.transformTo(AttributeValue.builder().n("180").build());
        assertEquals(Duration.ofSeconds(180), duration);
    }

    @Test
    void shouldWriteDurationAsIsoString() {
        AttributeValue value = converter.transformFrom(Duration.ofMinutes(8));
        assertEquals("PT8M", value.s());
    }

    @Test
    void shouldWriteNullDurationAsNullAttribute() {
        AttributeValue value = converter.transformFrom(null);
        assertTrue(value.nul());
    }

    @Test
    void shouldReturnNullWhenAttributeIsNull() {
        assertNull(converter.transformTo(null));
    }

    @Test
    void shouldReturnNullWhenNoDurationValuePresent() {
        assertNull(converter.transformTo(AttributeValue.builder().nul(true).build()));
    }

    @Test
    void shouldReturnNullWhenStringIsBlank() {
        assertNull(converter.transformTo(AttributeValue.builder().s("   ").build()));
    }

    @Test
    void shouldThrowExceptionForInvalidIsoDuration() {
        AttributeValue invalidAttribute = AttributeValue.builder().s("INVALID").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.transformTo(invalidAttribute));
        assertEquals("Cannot parse duration string: INVALID", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidNumericAttribute() {
        AttributeValue invalidAttribute = AttributeValue.builder().n("abc").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.transformTo(invalidAttribute));
        assertEquals("Cannot parse duration number: abc", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionForNonNumericValue() {
        AttributeValue invalidAttribute = AttributeValue.builder().n("12.34.56").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.transformTo(invalidAttribute));
        assertTrue(ex.getMessage().startsWith("Cannot parse duration number:"));
    }

    @Test
    void shouldThrowExceptionForInvalidStringWithExtraCharacters() {
        AttributeValue invalidAttribute = AttributeValue.builder().s("PT24H EXTRA").build();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> converter.transformTo(invalidAttribute));
        assertTrue(ex.getMessage().startsWith("Cannot parse duration string:"));
    }

    @Test
    void shouldParseValidIsoDurationWith25Hours() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT25H30M").build());
        assertEquals(Duration.ofHours(25).plusMinutes(30), duration);
    }

    @Test
    void shouldParseNegativeDurationFromNumber() {
        Duration duration = converter.transformTo(AttributeValue.builder().n("-3600").build());
        assertEquals(Duration.ofSeconds(-3600), duration);
    }

    @Test
    void shouldReturnDurationType() {
        assertEquals(Duration.class, converter.type().rawClass());
    }

    @Test
    void shouldReturnStringAttributeType() {
        assertEquals(AttributeValueType.S, converter.attributeValueType());
    }
}
