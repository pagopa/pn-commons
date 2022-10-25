package it.pagopa.pn.commons.abstractions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.abstractions.ParameterConsumer;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;

@Slf4j
public class AbstractCachedSsmParameterConsumer implements ParameterConsumer {

    private final SsmClient ssmClient;
    private final Duration cacheExpiration = Duration.of(5, ChronoUnit.MINUTES);

    public AbstractCachedSsmParameterConsumer(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    private final ConcurrentHashMap<String, ExpiringValue> valueCache = new ConcurrentHashMap<>();
    public <T> Optional<T> getParameterValue( String parameterName, Class<T> clazz ) {
        Optional<T> optValue = (Optional<T>) valueCache.computeIfAbsent( parameterName, key -> new ExpiringValue())
                .getValueCheckTimestamp();
        if ( optValue == null ) {
            optValue = getParameter( parameterName, clazz );
            valueCache.put( parameterName, new ExpiringValue(optValue, cacheExpiration));
        }
        return optValue;
    }


    public String getParameter(String parameterName) {
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(parameterName)
                .build();
        try {
            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
            return parameterResponse.parameter().value();
        } catch ( SsmException ex) {
            log.info( "Ssm Client exception for parameterName={}", parameterName, ex );
            return null;
        }
    }

    @NonNull
    private <T> Optional<T> getParameter(String parameterName, Class<T> clazz) {
        Optional<T> result = Optional.empty();
        String json = getParameter( parameterName );
        if (StringUtils.hasText( json )) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                result = Optional.of( objectMapper.readValue( json, clazz ) );
            } catch (JsonProcessingException e) {
                throw new PnInternalException( "Unable to deserialize object", ERROR_CODE_PN_GENERIC_ERROR, e );
            }
        }
        return result;
    }

    @Value
    private static class ExpiringValue {
        Object value;
        Instant timestamp;

        private ExpiringValue(){
            this.value = null;
            this.timestamp = Instant.EPOCH;
        }

        public ExpiringValue(Object value, Instant cacheExpiration) {
            this.value = value;
            this.timestamp = cacheExpiration;
        }

        public ExpiringValue(Object value, Duration cacheExpiration) {
            this (value, Instant.now().plus( cacheExpiration ));
        }

        public Object getValueCheckTimestamp() {
            Object result = null;
            if ( Instant.now().isBefore( timestamp ) ){
                result = value;
            }
            return result;
        }
    }
}
