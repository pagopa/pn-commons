package it.pagopa.pn.commons.exceptions;

public class PnHttpClientResponseException extends PnInternalException{
    
    public PnHttpClientResponseException(String message) {
        super(message);
    }

    public PnHttpClientResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
