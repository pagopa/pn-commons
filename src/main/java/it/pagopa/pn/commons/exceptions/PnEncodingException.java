package it.pagopa.pn.commons.exceptions;

public class PnEncodingException extends IllegalArgumentException{

    public PnEncodingException() {}

    public PnEncodingException(String message) {
        super(message);
    }

    public PnEncodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
