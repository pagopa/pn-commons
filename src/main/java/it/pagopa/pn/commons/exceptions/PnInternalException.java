package it.pagopa.pn.commons.exceptions;

public class PnInternalException extends RuntimeException {

    public PnInternalException(String message) {
        super(message);
    }

    public PnInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
