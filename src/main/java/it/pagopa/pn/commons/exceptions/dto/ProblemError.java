package it.pagopa.pn.commons.exceptions.dto;

import java.io.Serializable;

/**
 * ProblemError
 */
@lombok.Builder(toBuilder = true)
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Data
public class ProblemError  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String element;
    private String detail;
}

