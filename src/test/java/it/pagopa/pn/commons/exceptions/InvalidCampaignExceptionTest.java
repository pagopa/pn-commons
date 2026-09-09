package it.pagopa.pn.commons.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvalidCampaignExceptionTest {

    @Test
    void shouldBuildExpectedProblem() {
        InvalidCampaignException exception = new InvalidCampaignException("sender-1", "camp-1");

        assertEquals(500, exception.getProblem().getStatus());
        assertTrue(exception.getProblem().getDetail().contains("campaignId=camp-1"));
        assertTrue(exception.getProblem().getDetail().contains("senderId=sender-1"));
        assertEquals("INVALID_CAMPAIGN", exception.getProblem().getErrors().get(0).getCode());
    }
}

