package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.util.Optional;

public class AbstractSsmParameterConsumer <T> implements ParameterConsumer <T> {

    private final SsmClient ssmClient;

    public AbstractSsmParameterConsumer(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    @Override
    public String getParameter(String parameterName) {
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(parameterName)
                .build();

        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        return parameterResponse.parameter().value();
    }

    @Override
    public Optional<T> getParameter(String fieldName, Class<T> clazz) {
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(fieldName)
                .build();

        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        return parameterResponse.getValueForField( fieldName, clazz );
    }
}
