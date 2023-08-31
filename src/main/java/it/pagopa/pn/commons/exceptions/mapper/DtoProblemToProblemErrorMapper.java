package it.pagopa.pn.commons.exceptions.mapper;

import it.pagopa.pn.commons.exceptions.dto.ProblemError;

public class DtoProblemToProblemErrorMapper {

    private DtoProblemToProblemErrorMapper(){}

    public static ProblemError toProblemError(it.pagopa.pn.common.rest.error.v1.dto.ProblemError dtoProblemError)
    {
        return  ProblemError.builder()
                .code(dtoProblemError.getCode())
                .detail(dtoProblemError.getDetail())
                .element(dtoProblemError.getElement())
                .build();
    }

}
