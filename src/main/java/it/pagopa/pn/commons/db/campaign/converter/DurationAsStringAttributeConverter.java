package it.pagopa.pn.commons.db.campaign.converter;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DurationAsStringAttributeConverter implements AttributeConverter<Duration> {
    private static final Pattern SIMPLE_VALUE_WITH_UNIT = Pattern.compile("^(\\d+)([SMHD])$");
    private static final Pattern PT_WITHOUT_UNIT = Pattern.compile("^PT(\\d+)$");

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
            return parseDuration(attributeValue.s());
        }
        if (attributeValue != null && attributeValue.n() != null && !attributeValue.n().isBlank()) {
            try {
                return Duration.ofSeconds(Long.parseLong(attributeValue.n().trim()));
            } catch (NumberFormatException ex) {
                log.warn("Unsupported numeric duration value '{}'", attributeValue.n());
                return null;
            }
        }
        return null;
    }

    private Duration parseDuration(String rawValue) {
        String normalized = normalize(rawValue);
        if (normalized == null) {
            return null;
        }

        try {
            return Duration.parse(normalized);
        } catch (DateTimeParseException ex) {
            if (normalized.chars().allMatch(Character::isDigit)) {
                return Duration.ofSeconds(Long.parseLong(normalized));
            }

            Matcher simpleValueMatcher = SIMPLE_VALUE_WITH_UNIT.matcher(normalized);
            if (simpleValueMatcher.matches()) {
                long value = Long.parseLong(simpleValueMatcher.group(1));
                String unit = simpleValueMatcher.group(2);
                if ("S".equals(unit)) {
                    return Duration.ofSeconds(value);
                }
                if ("M".equals(unit)) {
                    return Duration.ofMinutes(value);
                }
                if ("H".equals(unit)) {
                    return Duration.ofHours(value);
                }
                if ("D".equals(unit)) {
                    return Duration.ofDays(value);
                }
                return null;
            }

            Matcher ptWithoutUnitMatcher = PT_WITHOUT_UNIT.matcher(normalized);
            if (ptWithoutUnitMatcher.matches()) {
                return Duration.ofSeconds(Long.parseLong(ptWithoutUnitMatcher.group(1)));
            }

            log.warn("Unsupported duration value '{}'", rawValue);
            return null;
        }
    }

    private String normalize(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        String normalized = rawValue.trim();
        if (normalized.length() >= 2 && normalized.startsWith("\"") && normalized.endsWith("\"")) {
            normalized = normalized.substring(1, normalized.length() - 1).trim();
        }
        if (normalized.isBlank()) {
            return null;
        }
        return normalized.toUpperCase();
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
