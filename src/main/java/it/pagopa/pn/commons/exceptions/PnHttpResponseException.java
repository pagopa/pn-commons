package it.pagopa.pn.commons.exceptions;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import lombok.Getter;

import java.util.List;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_HTTPRESPONSE_GENERIC_ERROR;

@Getter
public class PnHttpResponseException extends PnRuntimeException{
    private final int statusCode;

    public PnHttpResponseException(String message, int statusCode) {
        super(message, message, statusCode, ERROR_CODE_PN_HTTPRESPONSE_GENERIC_ERROR, null, null);
        this.statusCode = statusCode;
    }

    public PnHttpResponseException(String message, String description, int statusCode, List<ProblemError> problems, Exception cause) {
        super(message, description, statusCode, problems, cause );
        this.statusCode = statusCode;
    }
}
