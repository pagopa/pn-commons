package it.pagopa.pn.commons.exceptions;


import it.pagopa.pn.common.rest.error.v1.dto.Problem;

public interface IPnException {

    /**
     * Ritorna il Problem da tornare al frontend a partire dalle informazioni presenti nell'exception
     * @return Problem contenente le info da usare nella risposta
     */
    Problem getProblem();

    /**
     * Ritorna il codice http da usare nella risposta in base all'exception
     * @return intero contenente il codice http della risposta
     */
    default int getStatus() {
        return getProblem().getStatus();
    }
}
