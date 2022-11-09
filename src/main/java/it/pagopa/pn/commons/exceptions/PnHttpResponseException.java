package it.pagopa.pn.commons.exceptions;

import lombok.Getter;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_HTTPRESPONSE_GENERIC_ERROR;

@Getter
public class PnHttpResponseException extends PnInternalException{
    private final int statusCode;
    
    public PnHttpResponseException(String message, int statusCode) {
        super(message, ERROR_CODE_PN_HTTPRESPONSE_GENERIC_ERROR);
        this.statusCode = statusCode;
    }
}
