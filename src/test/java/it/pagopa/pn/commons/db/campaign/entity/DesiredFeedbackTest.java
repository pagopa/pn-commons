package it.pagopa.pn.commons.db.campaign.entity;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesiredFeedbackTest {

    @Test
    void shouldExposeAllExpectedEnumValues() {
        Set<String> values = Set.of(DesiredFeedback.values()).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        assertEquals(5, values.size());
        assertTrue(values.contains("READ"));
        assertTrue(values.contains("RECEIVED"));
        assertTrue(values.contains("PAID"));
        assertTrue(values.contains("SENT"));
        assertTrue(values.contains("SKIP"));
    }

    @Test
    void shouldResolveEnumFromValue() {
        assertEquals(DesiredFeedback.READ, DesiredFeedback.valueOf("READ"));
        assertEquals(DesiredFeedback.RECEIVED, DesiredFeedback.valueOf("RECEIVED"));
        assertEquals(DesiredFeedback.PAID, DesiredFeedback.valueOf("PAID"));
        assertEquals(DesiredFeedback.SENT, DesiredFeedback.valueOf("SENT"));
        assertEquals(DesiredFeedback.SKIP, DesiredFeedback.valueOf("SKIP"));
    }
}

