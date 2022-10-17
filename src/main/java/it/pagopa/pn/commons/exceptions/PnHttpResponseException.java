package it.pagopa.pn.commons.exceptions;

import lombok.Getter;

@Getter
public class PnHttpResponseException extends PnInternalException{
    private final int statusCode;
    
    public PnHttpResponseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
