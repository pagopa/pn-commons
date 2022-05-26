package it.pagopa.pn.commons.exceptions;

public class PnHttpServerResponseException extends PnInternalException{
    public PnHttpServerResponseException(String message) {
        super(message);
    }

    public PnHttpServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
