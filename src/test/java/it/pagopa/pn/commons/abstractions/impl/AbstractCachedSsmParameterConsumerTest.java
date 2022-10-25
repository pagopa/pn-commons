package it.pagopa.pn.commons.abstractions.impl;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCachedSsmParameterConsumerTest {

    @Mock
    private SsmClient ssmClient;

    private AbstractCachedSsmParameterConsumer consumer;

    @BeforeEach
    void setup() {
        this.consumer = new AbstractCachedSsmParameterConsumer( ssmClient );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getParameterValueSuccess() {

        GetParameterResponse response = GetParameterResponse.builder()
                .parameter( Parameter.builder()
                        .name( "parameterName" )
                        .value( "\"parameterValue\"" )
                        .build() )
                .build();

        Mockito.when( ssmClient.getParameter( Mockito.any(GetParameterRequest.class) ) ).thenReturn( response );
        Optional<String> result = consumer.getParameterValue( "parameterName", String.class );

        assertNotNull( result );
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getParameterValueFailure() {

        GetParameterResponse response = GetParameterResponse.builder()
                .parameter( Parameter.builder()
                        .name( "parameterName" )
                        .value( "{\"parameterValue\"}" )
                        .build() )
                .build();

        Mockito.when( ssmClient.getParameter( Mockito.any(GetParameterRequest.class) ) ).thenReturn( response );
        Executable todo = () -> consumer.getParameterValue( "parameterName", String.class );

        assertThrows(PnInternalException.class, todo);
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void getParameterValueSuccessDefault() {

        Mockito.when( ssmClient.getParameter( Mockito.any(GetParameterRequest.class) ) ).thenThrow( SsmException.class );
        Optional<String> result = consumer.getParameterValue( "parameterName", String.class );

        assertEquals( Optional.empty(), result );
    }

}
