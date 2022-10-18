package it.pagopa.pn.commons.abstractions;

import java.util.Optional;

public interface ParameterConsumer <T> {

     String getParameter(String parameterName);

     Optional<T> getParameter(String fieldName, Class<T> clazz);

}
