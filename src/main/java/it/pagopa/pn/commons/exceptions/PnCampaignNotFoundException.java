package it.pagopa.pn.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PnCampaignNotFoundException extends PnRuntimeException {
    public static final String ERROR_CODE_CAMPAIGN_NOT_FOUND = "PN_CAMPAIGN_NOT_FOUND";

    public PnCampaignNotFoundException(String description) {
        super("Campaign not found", description, HttpStatus.NOT_FOUND.value(), ERROR_CODE_CAMPAIGN_NOT_FOUND, null, null);
    }
}

