package it.pagopa.pn.commons.abstractions;

import java.util.Map;

public class IdConflictException extends RuntimeException {

    private final transient Map<String,String> keyValueMap;

    public IdConflictException(Map<String, String> keyValueMap) {
        super(keyValueMap.toString());
        this.keyValueMap = keyValueMap;
    }

    public Map<String, String> getKeyValueMap() {
        return keyValueMap;
    }
}
