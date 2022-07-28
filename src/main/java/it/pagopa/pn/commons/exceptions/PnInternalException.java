package it.pagopa.pn.commons.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Eccezione di errore interno generico, viene tradotta in un errore 500
 * E' possibile crearla con un codice di errore opportuno
 */
public class PnInternalException extends PnRuntimeException {

    public PnInternalException(String message) {
        this(message, ExceptionHelper.ERROR_CODE_GENERIC_ERROR, null);
    }

    public PnInternalException(String message, Throwable cause) {
        this(message, ExceptionHelper.ERROR_CODE_GENERIC_ERROR, cause);
    }

    public PnInternalException(String message, String errorCode) {
        this(message, errorCode, null);
    }

    public PnInternalException(String message, String errorCode, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message, HttpStatus.INTERNAL_SERVER_ERROR.value(), errorCode, null, null, cause);
    }
}
