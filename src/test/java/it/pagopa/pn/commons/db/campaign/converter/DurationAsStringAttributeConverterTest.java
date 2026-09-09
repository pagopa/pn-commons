package it.pagopa.pn.commons.db.campaign.converter;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DurationAsStringAttributeConverterTest {
    private final DurationAsStringAttributeConverter converter = new DurationAsStringAttributeConverter();

    @Test
    void shouldParseIsoDuration() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT24H").build());
        assertEquals(Duration.ofHours(24), duration);
    }

    @Test
    void shouldParseIsoDurationWithSpacesAndLowercase() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("  pt8m ").build());
        assertEquals(Duration.ofMinutes(8), duration);
    }

    @Test
    void shouldParseSimpleDurationWithHoursUnit() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("24H").build());
        assertEquals(Duration.ofHours(24), duration);
    }

    @Test
    void shouldParseSimpleDurationWithMinutesUnit() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("15M").build());
        assertEquals(Duration.ofMinutes(15), duration);
    }

    @Test
    void shouldParseSimpleDurationWithSecondsUnit() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("45S").build());
        assertEquals(Duration.ofSeconds(45), duration);
    }

    @Test
    void shouldParseSimpleDurationWithDaysUnit() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("2D").build());
        assertEquals(Duration.ofDays(2), duration);
    }

    @Test
    void shouldParsePtWithoutUnitAsSeconds() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("PT5").build());
        assertEquals(Duration.ofSeconds(5), duration);
    }

    @Test
    void shouldParseNumericDurationAsSeconds() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("120").build());
        assertEquals(Duration.ofSeconds(120), duration);
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
    void shouldReturnNullForUnsupportedUnit() {
        assertNull(converter.transformTo(AttributeValue.builder().s("PT2Q").build()));
    }

    @Test
    void shouldReturnNullForInvalidNumericAttribute() {
        assertNull(converter.transformTo(AttributeValue.builder().n("12.3").build()));
    }

    @Test
    void shouldParseQuotedIsoDuration() {
        Duration duration = converter.transformTo(AttributeValue.builder().s("\"PT8M\"").build());
        assertEquals(Duration.ofMinutes(8), duration);
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
