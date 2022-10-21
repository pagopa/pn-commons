package it.pagopa.pn.commons.abstractions;

import java.util.Optional;

public interface ParameterConsumer {

     <T> Optional<T> getParameterValue( String parameterName, Class<T> clazz );
}
