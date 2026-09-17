package it.pagopa.pn.commons.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PnCampaignNotFoundExceptionTest {

    @Test
    void shouldBuildExpectedProblem() {
        PnCampaignNotFoundException exception = new PnCampaignNotFoundException("campaign missing");

        assertEquals(404, exception.getProblem().getStatus());
        assertEquals("Campaign not found", exception.getProblem().getTitle());
        assertEquals("campaign missing", exception.getProblem().getDetail());
        assertEquals(PnCampaignNotFoundException.ERROR_CODE_CAMPAIGN_NOT_FOUND,
                exception.getProblem().getErrors().get(0).getCode());
    }
}

