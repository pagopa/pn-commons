package it.pagopa.pn.commons.abstractions;

public class IdConflictException extends Exception {

    public <V> IdConflictException(V value) {
        super( "Duplicated Id on key value store " + value );
    }
}
