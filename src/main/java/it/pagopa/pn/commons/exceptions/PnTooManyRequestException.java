package it.pagopa.pn.commons.exceptions;

import org.springframework.http.HttpStatus;

public class PnTooManyRequestException extends PnRuntimeException {

    public PnTooManyRequestException(String message, Throwable cause) {
        this(message, PnExceptionsCodes.ERROR_CODE_PN_TOO_MANY_REQUESTS, cause);
    }

    public PnTooManyRequestException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public PnTooManyRequestException(String message, String errorCode, Throwable cause) {
        super(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), message, HttpStatus.TOO_MANY_REQUESTS.value(), errorCode, null, null, cause);
    }

}
