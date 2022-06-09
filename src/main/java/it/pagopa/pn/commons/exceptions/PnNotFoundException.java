package it.pagopa.pn.commons.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PnNotFoundException extends RuntimeException {
    public PnNotFoundException(String message) {
        super(message);
    }
}
